package stuthemp.recipes.recipes_app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
            StringBuilder sb = new StringBuilder(
                    "SELECT distinct d.id " +
                            "FROM dishes d "
            );
            sb.append("JOIN dish_ingredients di ON d.id = di.dish_id ");
            sb.append("JOIN ingredients i ON di.ingredient_id = i.id ");
            sb.append("JOIN dish_cook_process dcp ON d.id = dcp.dish_id ");
            sb.append("JOIN cook_processes cp ON dcp.cook_process_id = cp.id ");
            sb.append("JOIN dish_cuisines dc ON d.id = dc.dish_id ");
            sb.append("JOIN cuisine c ON dc.cuisine_id = c.id ");
            sb.append("WHERE 1=1 ");

            if (dishSearchDto.getIsMeaty() != null) {
                if (dishSearchDto.getIsMeaty()) {
                    sb.append("AND d.has_meat IS TRUE ");
                } else {
                    sb.append("AND d.has_meat IS FALSE ");
                }
            }

            if (dishSearchDto.getIsExpensive() != null) {
                if (dishSearchDto.getIsExpensive()) {
                    sb.append("AND d.is_expensive IS TRUE ");
                } else {
                    sb.append("AND d.is_expensive IS FALSE ");
                }
            }

            if (dishSearchDto.getPreparationNeeded() != null) {
                if (dishSearchDto.getPreparationNeeded()) {
                    sb.append("AND d.preparation_needed IS TRUE ");
                } else {
                    sb.append("AND d.preparation_needed IS FALSE ");
                }
            }

            if (dishSearchDto.getIsSour() != null) {
                if (dishSearchDto.getIsSour()) {
                    sb.append("AND d.is_sour IS TRUE ");
                } else {
                    sb.append("AND d.is_sour IS FALSE ");
                }
            }

            if (dishSearchDto.getIsSweet() != null) {
                if (dishSearchDto.getIsSweet()) {
                    sb.append("AND d.is_sweet IS TRUE ");
                } else {
                    sb.append("AND d.is_sweet IS FALSE ");
                }
            }

            if (dishSearchDto.getIsSoup() != null) {
                if (dishSearchDto.getIsSoup()) {
                    sb.append("AND d.is_soup IS TRUE ");
                } else {
                    sb.append("AND d.is_soup IS FALSE ");
                }
            }

            if (dishSearchDto.getIsDietary() != null) {
                if (dishSearchDto.getIsDietary()) {
                    sb.append("AND d.is_dietary IS TRUE ");
                } else {
                    sb.append("AND d.is_dietary IS FALSE ");
                }
            }

            if (dishSearchDto.getIsFat() != null) {
                if (dishSearchDto.getIsFat()) {
                    sb.append("AND d.is_fat IS TRUE ");
                } else {
                    sb.append("AND d.is_fat IS FALSE ");
                }
            }

            if(dishSearchDto.getTime() != null) {
                if(dishSearchDto.getTime().getGt() != null) {
                    sb.append("AND d.cooking_time > ").append(dishSearchDto.getTime().getGt()).append(" ");
                }
                if(dishSearchDto.getTime().getLt() != null) {
                    sb.append("AND d.cooking_time < ").append(dishSearchDto.getTime().getLt()).append(" ");
                }
            }

            if(dishSearchDto.getName() != null && !dishSearchDto.getName().trim().isEmpty()) {
                sb.append("AND d.name LIKE '%").append(dishSearchDto.getName()).append("%' ");
            }

            if(dishSearchDto.getInstruction() != null) {
                sb.append("AND d.instruction LIKE '%").append(dishSearchDto.getInstruction()).append("%' ");
            }


            if (dishSearchDto.getCuisines() != null) {
                if (!dishSearchDto.getCuisines().getInclude().isEmpty()) {
                    sb.append("AND c.name IN (");
                    for (int j = 0; j < dishSearchDto.getCuisines().getInclude().size(); j++) {
                        sb.append("'").append(dishSearchDto.getCuisines().getInclude().get(j)).append("'");
                        if (j < dishSearchDto.getCuisines().getInclude().size() - 1) {
                            sb.append(", ");
                        }
                    }
                    sb.append(") ");
                }
            }


            if(dishSearchDto.getIngredients() != null) {
                if (!dishSearchDto.getIngredients().getInclude().isEmpty()) {
                    sb.append("AND i.name IN (");
                    for (int j = 0; j < dishSearchDto.getIngredients().getInclude().size(); j++) {
                        sb.append("'").append(dishSearchDto.getIngredients().getInclude().get(j)).append("'");
                        if (j < dishSearchDto.getIngredients().getInclude().size() - 1) {
                            sb.append(", ");
                        }
                    }
                    sb.append(") ");
                }

                if (!dishSearchDto.getIngredients().getExclude().isEmpty()) {
                    sb.append("AND i.name NOT IN (");
                    for (int j = 0; j < dishSearchDto.getIngredients().getExclude().size(); j++) {
                        sb.append("'").append(dishSearchDto.getIngredients().getExclude().get(j)).append("'");
                        if (j < dishSearchDto.getIngredients().getExclude().size() - 1) {
                            sb.append(", ");
                        }
                    }
                    sb.append(") ");
                }
            }

            if(dishSearchDto.getCookProcess() != null) {
                if (!dishSearchDto.getCookProcess().getInclude().isEmpty()) {
                    sb.append("AND cp.name IN (");
                    for (int j = 0; j < dishSearchDto.getCookProcess().getInclude().size(); j++) {
                        sb.append("'").append(dishSearchDto.getCookProcess().getInclude().get(j)).append("'");
                        if (j < dishSearchDto.getCookProcess().getInclude().size() - 1) {
                            sb.append(", ");
                        }
                    }
                    sb.append(") ");
                }

                if (!dishSearchDto.getCookProcess().getExclude().isEmpty()) {
                    sb.append("AND cp.name NOT IN (");
                    for (int j = 0; j < dishSearchDto.getCookProcess().getExclude().size(); j++) {
                        sb.append("'").append(dishSearchDto.getCookProcess().getExclude().get(j)).append("'");
                        if (j < dishSearchDto.getCookProcess().getExclude().size() - 1) {
                            sb.append(", ");
                        }
                    }
                    sb.append(") ");
                }
            }

            log.info("Searching: " + sb.toString());
            List<Long> ids = findAllDishes((sb.toString()));

            return dishRepository.findAllById(ids);
        } catch (Exception e) {
            log.error("Error while looking for dish: " + e.getClass() + " with message " + e.getMessage());
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
