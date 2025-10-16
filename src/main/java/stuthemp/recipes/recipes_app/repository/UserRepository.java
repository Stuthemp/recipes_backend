package stuthemp.recipes.recipes_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stuthemp.recipes.recipes_app.model.RecipesUser;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<RecipesUser, Long> {
    Optional<RecipesUser> findByUsername(String username);
    boolean existsByUsername(String username);
}
