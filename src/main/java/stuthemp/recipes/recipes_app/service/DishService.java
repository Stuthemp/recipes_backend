package stuthemp.recipes.recipes_app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stuthemp.recipes.recipes_app.dto.request.search.DishSearchDto;
import stuthemp.recipes.recipes_app.model.CookProcess;
import stuthemp.recipes.recipes_app.model.Cuisine;
import stuthemp.recipes.recipes_app.model.Dish;
import stuthemp.recipes.recipes_app.model.Ingredient;
import stuthemp.recipes.recipes_app.repository.CookProcessRepository;
import stuthemp.recipes.recipes_app.repository.CuisineRepository;
import stuthemp.recipes.recipes_app.repository.DishRepository;
import stuthemp.recipes.recipes_app.repository.IngredientRepository;
import stuthemp.recipes.recipes_app.dto.request.creation.DishCreationDto;
import stuthemp.recipes.recipes_app.specifications.DishSpecifications;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class DishService {

    @Autowired
    DishRepository dishRepository;
    @Autowired
    IngredientRepository ingredientRepository;
    @Autowired
    CookProcessRepository cookProcessRepository;
    @Autowired
    CuisineRepository cuisineRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean create(DishCreationDto dishCreationDto) {
        try {
            Dish dish = new Dish();
            Set<CookProcess> cookProcessExists = cookProcessRepository.findByNameIn(dishCreationDto.getCookProcess());
            Set<Ingredient> ingredientsExists = ingredientRepository.findByNameIn(dishCreationDto.getIngredients());
            Set<Cuisine> cuisinesExists = cuisineRepository.findByNameIn(dishCreationDto.getCuisines());

            Set<String> missingIngredients = findMissingIngredientNames(ingredientsExists, dishCreationDto.getIngredients());
            Set<String> missingCookProcess = findMissingCookProcessNames(cookProcessExists, dishCreationDto.getCookProcess());
            Set<String> missingCuisines = findMissingCuisinesNames(cuisinesExists, dishCreationDto.getCuisines());

            dish.setCookProcess(cookProcessExists);
            for (String cookProcessName: missingCookProcess) {
                CookProcess cookProcess = new CookProcess();
                cookProcess.setName(cookProcessName);
                dish.getCookProcess().add(cookProcess);

                cookProcessRepository.save(cookProcess);
            }

            dish.setIngredients(ingredientsExists);
            for (String ingredientName: missingIngredients) {
                Ingredient ingredient = new Ingredient();
                ingredient.setName(ingredientName);
                dish.getIngredients().add(ingredient);

                ingredientRepository.save(ingredient);
            }

            dish.setCuisines(cuisinesExists);
            for (String cuisineName: missingCuisines) {
                Cuisine cuisine = new Cuisine();
                cuisine.setName(cuisineName);
                dish.getCuisines().add(cuisine);

                cuisineRepository.save(cuisine);
            }

            dish.setName(dishCreationDto.getName());
            dish.setTime(dishCreationDto.getTime());
            dish.setUrl(dishCreationDto.getUrl());

            dish.setIsSoup(dishCreationDto.getIsSoup());
            dish.setIsSour(dishCreationDto.getIsSour());
            dish.setIsSweet(dishCreationDto.getIsSweet());
            dish.setIsFat(dishCreationDto.getIsFat());
            dish.setIsDietary(dishCreationDto.getIsDietary());
            dish.setIsSeafood(dishCreationDto.getIsSeafood());
            dish.setIsSpicy(dishCreationDto.getIsSpicy());
            dish.setIsMeaty(dishCreationDto.getIsMeaty());
            dish.setIsExpensive(dishCreationDto.getIsExpensive());
            dish.setPreparationNeeded(dishCreationDto.getPreparationNeeded());

            dishRepository.save(dish);
            return true;
        } catch (Exception e) {
            log.error("Error while creating dish: " + e.getClass() + " with message " + e.getMessage());
            return false;
        }
    }

    @Transactional
    public Iterable<Dish> findAll(DishSearchDto dishSearchDto) {
        try {
            Specification<Dish> spec = DishSpecifications.bySearchDto(dishSearchDto);
            List<Dish> result = dishRepository.findAll(spec).stream().distinct().toList();
            log.info("Found {} dishes matching criteria", result.size());
            return result;
        } catch (Exception e) {
            log.error("Error while searching dishes: {} - {}", e.getClass(), e.getMessage());
            return List.of();
        }
    }

    private List<Long> findAllDishes(String sql) {
       return jdbcTemplate.queryForList(sql, Long.class);
    }

    public static Set<String> findMissingIngredientNames(Set<Ingredient> ingredients, Set<String> names) {
        // Create a set of ingredient names
        Set<String> ingredientNames = ingredients.stream()
                .map(Ingredient::getName)
                .collect(Collectors.toSet());

        // Find names in the names set that are not in ingredientNames
        return names.stream()
                .filter(name -> !ingredientNames.contains(name))
                .collect(Collectors.toSet());
    }

    public static Set<String> findMissingCookProcessNames(Set<CookProcess> ingredients, Set<String> names) {
        // Create a set of ingredient names
        Set<String> ingredientNames = ingredients.stream()
                .map(CookProcess::getName)
                .collect(Collectors.toSet());

        // Find names in the names set that are not in ingredientNames
        return names.stream()
                .filter(name -> !ingredientNames.contains(name))
                .collect(Collectors.toSet());
    }

    public static Set<String> findMissingCuisinesNames(Set<Cuisine> cuisines, Set<String> names) {
        // Create a set of ingredient names
        Set<String> cuisinesNames = cuisines.stream()
                .map(Cuisine::getName)
                .collect(Collectors.toSet());

        // Find names in the names set that are not in ingredientNames
        return names.stream()
                .filter(name -> !cuisinesNames.contains(name))
                .collect(Collectors.toSet());
    }

    @Transactional
    public List<String> findAllIngredients() {
        return StreamSupport.stream(ingredientRepository.findAll().spliterator(), false)
                .map(Ingredient::getName)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<String> findAllCookProcesses() {
        return StreamSupport.stream(cookProcessRepository.findAll().spliterator(), false)
                .map(CookProcess::getName)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<String> findAllCuisines() {
        return StreamSupport.stream(cuisineRepository.findAll().spliterator(), false)
                .map(Cuisine::getName)
                .collect(Collectors.toList());
    }

}
