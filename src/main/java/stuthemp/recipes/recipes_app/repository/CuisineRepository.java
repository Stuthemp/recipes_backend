package stuthemp.recipes.recipes_app.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import stuthemp.recipes.recipes_app.model.Cuisine;
import stuthemp.recipes.recipes_app.model.Ingredient;

import java.util.Set;

@Repository
public interface CuisineRepository extends CrudRepository<Cuisine, Long> {
    Set<Cuisine> findByNameIn(Set<String> names);
}
