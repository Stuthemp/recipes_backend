package stuthemp.recipes.recipes_app.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import stuthemp.recipes.recipes_app.model.CookProcess;
import stuthemp.recipes.recipes_app.model.Ingredient;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CookProcessRepository extends CrudRepository<CookProcess, Long> {
    Optional<CookProcess> findByName(String name);
    Set<CookProcess> findByNameIn(Set<String> names);
}
