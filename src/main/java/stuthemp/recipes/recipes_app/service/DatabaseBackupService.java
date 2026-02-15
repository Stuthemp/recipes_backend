package stuthemp.recipes.recipes_app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
public class DatabaseBackupService {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${backup.s3.bucket}")
    private String s3Bucket;

    @Value("${backup.s3.region}")
    private String s3Region;

    @Value("${backup.s3.url}")
    private String s3Url;

    @Value("${backup.s3.access-key}")
    private String s3AccessKey;

    @Value("${backup.s3.secret-key}")
    private String s3SecretKey;

    @Scheduled(cron = "0 0 20 * * *")
    public void createDailyBackup() {
        try {
            String backupFile = createDatabaseBackup();
            uploadToS3(backupFile);
            cleanupLocalFiles();
        } catch (Exception e) {
            log.error("Backup failed: {}", e.getMessage());
        }
    }

    private String createDatabaseBackup() throws IOException, InterruptedException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupPath = "/tmp/backups/db_backup_" + timestamp + ".sql";

        // Создание директории
        Path dir = Files.createDirectories(Paths.get("/tmp/backups"));
        log.info("Directory created: {}", dir.getFileName());

        DatabaseConnectionInfo connInfo = extractPostgresConnectionInfo(dbUrl);

        // Путь к pg_dump (кроссплатформенный)
        String pgDumpCmd = "/usr/bin/pg_dump";

        if (!Files.exists(Paths.get(pgDumpCmd))) {
            throw new IOException("pg_dump not found at " + pgDumpCmd +
                    ". Ensure postgresql-client is installed in the container.");
        }

        ProcessBuilder pb = new ProcessBuilder(
                pgDumpCmd,
                "-h", connInfo.host,
                "-p", connInfo.port,
                "-U", dbUsername,
                "-d", connInfo.database,
                "-f", backupPath
        );

        // Передача пароля через переменную окружения (безопаснее)
        pb.environment().put("PGPASSWORD", dbPassword);
        pb.redirectErrorStream(true);

        log.info("Starting pg_dump process...");
        Process process = pb.start();

        // Чтение вывода для логирования ошибок
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            log.info("pg_dump: {}", line);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("pg_dump failed with exit code " + exitCode);
        }

        log.info("Backup created successfully: {}", backupPath);
        return backupPath;
    }

    // Извлечение параметров из JDBC URL PostgreSQL
    private DatabaseConnectionInfo extractPostgresConnectionInfo(String dbUrl) {
        // Формат: jdbc:postgresql://host:port/database
        String url = dbUrl.replace("jdbc:postgresql://", "");

        // Разделяем на хост:порт и базу данных
        int slashIndex = url.indexOf('/');
        String hostPort = url.substring(0, slashIndex);
        String database = url.substring(slashIndex + 1);

        // Разделяем хост и порт
        String[] hostPortParts = hostPort.split(":");
        String host = hostPortParts[0];
        String port = hostPortParts.length > 1 ? hostPortParts[1] : "5432"; // порт по умолчанию

        return new DatabaseConnectionInfo(host, port, database);
    }

    private void uploadToS3(String filePath) {
        S3Client s3Client = S3Client.builder()
                .region(Region.of(s3Region))
                .endpointOverride(URI.create(s3Url))
                .credentialsProvider(() -> AwsBasicCredentials.create(
                        s3AccessKey,
                        s3SecretKey
                ))
                .build();

        String key = "backups/" + Paths.get(filePath).getFileName().toString();

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(s3Bucket)
                        .key(key)
                        .build(),
                RequestBody.fromFile(Paths.get(filePath))
        );
    }

    private String extractDatabaseName(String url) {
        // Извлечение имени БД из URL
        return url.substring(url.lastIndexOf("/") + 1);
    }

    private void cleanupLocalFiles() {
        // Удаление старых файлов (>7 дней)
        try {
            Files.walk(Paths.get("/tmp/backups"))
                    .filter(path -> Files.isRegularFile(path))
                    .filter(path -> {
                        try {
                            return Files.getLastModifiedTime(path).toInstant()
                                    .isBefore(Instant.now().minus(7, ChronoUnit.DAYS));
                        } catch (IOException e) {
                            return false;
                        }
                    })
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            System.err.println("Failed to delete: " + path);
                        }
                    });
        } catch (IOException e) {
            System.err.println("Cleanup failed: " + e.getMessage());
        }
    }

    // Вспомогательный класс для хранения параметров подключения
    private static class DatabaseConnectionInfo {
        String host;
        String port;
        String database;

        DatabaseConnectionInfo(String host, String port, String database) {
            this.host = host;
            this.port = port;
            this.database = database;
        }
    }
}
