package stuthemp.recipes.recipes_app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stuthemp.recipes.recipes_app.model.RecipesRole;
import stuthemp.recipes.recipes_app.model.RecipesUser;
import stuthemp.recipes.recipes_app.repository.UserRepository;

import java.util.Set;

@Service
@Order(1)
@Slf4j
public class Bootstrap implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${stuthemp.recipes.admin.login}")
    private String adminUserName;

    @Value("${stuthemp.recipes.admin.password}")
    private String adminPassword;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Creating admin with credentials: " + adminUserName + "_" + adminPassword + "_");
        if (!userRepository.existsByUsername(adminUserName)) {
            RecipesUser user = new RecipesUser(
                    adminUserName,
                    passwordEncoder.encode(adminPassword),
                    Set.of(RecipesRole.ROLE_USER, RecipesRole.ROLE_ADMIN)
            );

            userRepository.save(user);
        }
    }

}
