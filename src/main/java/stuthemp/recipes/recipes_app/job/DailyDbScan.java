package stuthemp.recipes.recipes_app.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import stuthemp.recipes.recipes_app.dto.request.creation.DishCreationDto;
import stuthemp.recipes.recipes_app.model.CookProcess;
import stuthemp.recipes.recipes_app.model.Cuisine;
import stuthemp.recipes.recipes_app.model.Dish;
import stuthemp.recipes.recipes_app.model.Ingredient;
import stuthemp.recipes.recipes_app.repository.DishRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DailyDbScan {

    @Autowired
    DishRepository dishRepository;

    @Autowired
    private JavaMailSender emailSender;

    @Value("${stuthemp.recipes.smtp.recipient}")
    String recipient;

    @Value("${stuthemp.recipes.smtp.sender}")
    String sender;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Scheduled(cron = "0 55 23 * * *")
    @Transactional
    public void createBackup() {
        System.out.println("Starting backup");
        Iterable<Dish> dishes = dishRepository.findAll();
        List<DishCreationDto> dishCreationDtos = new ArrayList<>();

        for(Dish dish: dishes) {
            dishCreationDtos.add(dishToDto(dish));
        }

        log.debug("Dishes collected, size: " + dishCreationDtos.size());

        String json;
        try {
            json = objectMapper.writeValueAsString(dishCreationDtos);
        } catch (JsonProcessingException e) {
            log.error("Error converting dishes to JSON: " + e.getClass() + ":" + e.getMessage());
            return;
        }

        sendEmail(json);
    }

    private void sendEmail(String json) {
        System.out.println("Sending email");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient);
        message.setSubject("Dish Backup");
        message.setFrom(sender);
        message.setText(json);

        try {
            emailSender.send(message);
            log.debug("Email sent");
        } catch (Exception e) {
            log.error("Error sending email: " + e.getMessage());
        }
    }
    private DishCreationDto dishToDto(Dish dish) {
        DishCreationDto dto = new DishCreationDto();

        Set<String> ingredients = dish.getIngredients().stream().map(Ingredient::getName).collect(Collectors.toSet());
        dto.setIngredients(ingredients);

        Set<String> cookProcess = dish.getCookProcess().stream().map(CookProcess::getName).collect(Collectors.toSet());
        dto.setCookProcess(cookProcess);

        Set<String> cuisines = dish.getCuisines().stream().map(Cuisine::getName).collect(Collectors.toSet());
        dto.setCuisines(cuisines);

        dto.setTime(dish.getTime());
        dto.setUrl(dish.getUrl());
        dto.setPreparationNeeded(dish.getPreparationNeeded());
        dto.setIsExpensive(dish.getIsExpensive());
        dto.setName(dish.getName());
        dto.setIsMeaty(dish.getIsMeaty());
        dto.setInstruction(dish.getInstruction());

        dto.setIsSpicy(dish.getIsSpicy());
        dto.setIsSeafood(dish.getIsSeafood());
        dto.setIsSour(dish.getIsSour());
        dto.setIsSweet(dish.getIsSweet());
        dto.setIsSoup(dish.getIsSoup());
        dto.setIsDietary(dish.getIsDietary());
        dto.setIsFat(dish.getIsFat());

        return dto;
    }
}
