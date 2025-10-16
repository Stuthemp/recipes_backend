package stuthemp.recipes.recipes_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;

@SpringBootApplication
@Controller
@EnableScheduling
public class RecipesAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecipesAppApplication.class, args);
	}

}
