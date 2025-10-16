package stuthemp.recipes.recipes_app.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import stuthemp.recipes.recipes_app.model.Dish;

@Repository
public interface DishRepository extends CrudRepository<Dish, Long> {
}
