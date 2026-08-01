package stuthemp.recipes.recipes_app.specifications;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import stuthemp.recipes.recipes_app.dto.request.search.*;
import stuthemp.recipes.recipes_app.model.CookProcess;
import stuthemp.recipes.recipes_app.model.Dish;
import stuthemp.recipes.recipes_app.model.Ingredient;

public class DishSpecifications {

    public static Specification<Dish> hasBooleanField(Boolean value, String fieldName) {
        return (root, query, cb) -> value != null ? cb.equal(root.get(fieldName), value) : null;
    }

    public static Specification<Dish> byName(String name) {
        return (root, query, cb) -> name != null && !name.trim().isEmpty()
                ? cb.like(root.get("name"), "%" + name + "%")
                : null;
    }

    public static Specification<Dish> byInstruction(String instruction) {
        return (root, query, cb) -> instruction != null
                ? cb.like(root.get("instruction"), "%" + instruction + "%")
                : null;
    }

    public static Specification<Dish> byTimeRange(TimeConstraints time) {
        return (root, query, cb) -> {
            if (time == null) return null;
            Predicate predicate = null;
            if (time.getGt() != null) {
                predicate = cb.greaterThan(root.get("time"), time.getGt());
            }
            if (time.getLt() != null) {
                Predicate ltPredicate = cb.lessThan(root.get("time"), time.getLt());
                predicate = predicate == null ? ltPredicate : cb.and(predicate, ltPredicate);
            }
            return predicate;
        };
    }

    public static Specification<Dish> byIngredients(IngredientsDto ingredients) {
        return (root, query, cb) -> {
            if (ingredients == null) return null;
            Predicate  predicate = null;

            if (!ingredients.getInclude().isEmpty()) {
                Join<Dish, Ingredient> join = root.join("ingredients");
                predicate = join.get("name").in(ingredients.getInclude());
            }
            if (!ingredients.getExclude().isEmpty()) {
                Join<Dish, Ingredient> join = root.join("ingredients");
                Predicate excludePredicate = join.get("name").in(ingredients.getExclude()).not();
                predicate = predicate == null ? excludePredicate : cb.and(predicate, excludePredicate);
            }
            return predicate;
        };
    }

    public static Specification<Dish> byCookProcess(CookProcessDto cookProcess) {
        return (root, query, cb) -> {
            if (cookProcess == null) return null;
            Predicate predicate= null;

            if (!cookProcess.getInclude().isEmpty()) {
                Join<Dish, CookProcess> join = root.join("cookProcess");
                predicate = join.get("name").in(cookProcess.getInclude());
            }
            if (!cookProcess.getExclude().isEmpty()) {
               Join<Dish, CookProcess> join = root.join("cookProcess");
               Predicate excludePredicate = join.get("name").in(cookProcess.getExclude()).not();
               predicate = predicate == null ? excludePredicate : cb.and(predicate, excludePredicate);
            }
            return predicate;
        };
    }

    public static Specification<Dish> byCuisines(CuisineDto cuisines) {
        return (root, query, cb) -> {
            if (cuisines == null || cuisines.getInclude().isEmpty()) return null;
            var join = root.join("cuisines");
            return join.get("name").in(cuisines.getInclude());
        };
    }

    public static Specification<Dish> bySearchDto(DishSearchDto dto) {
        Specification<Dish> spec = Specification.where(null);

        spec = spec.and(hasBooleanField(dto.getIsMeaty(), "isMeaty"));
        spec = spec.and(hasBooleanField(dto.getIsExpensive(), "isExpensive"));
        spec = spec.and(hasBooleanField(dto.getPreparationNeeded(), "preparationNeeded"));
        spec = spec.and(hasBooleanField(dto.getIsSour(), "isSour"));
        spec = spec.and(hasBooleanField(dto.getIsSweet(), "isSweet"));
        spec = spec.and(hasBooleanField(dto.getIsSoup(), "isSoup"));
        spec = spec.and(hasBooleanField(dto.getIsDietary(), "isDietary"));
        spec = spec.and(hasBooleanField(dto.getIsFat(), "isFat"));

        spec = spec.and(byName(dto.getName()));
        spec = spec.and(byInstruction(dto.getInstruction()));
        spec = spec.and(byTimeRange(dto.getTime()));
        spec = spec.and(byIngredients(dto.getIngredients()));
        spec = spec.and(byCookProcess(dto.getCookProcess()));
        spec = spec.and(byCuisines(dto.getCuisines()));

        return spec;
    }
}
