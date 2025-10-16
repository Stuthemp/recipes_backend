package stuthemp.recipes.recipes_app.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import stuthemp.recipes.recipes_app.model.Ingredient;
import java.util.Set;

@Repository
public interface IngredientRepository extends CrudRepository<Ingredient, Long> {
    Set<Ingredient> findByNameIn(Set<String> names);

}
