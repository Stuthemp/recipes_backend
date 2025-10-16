package stuthemp.recipes.recipes_app.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class RecipesUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<RecipesRole> roles = new HashSet<>();

    public RecipesUser() {}

    public RecipesUser(String username, String password, Set<RecipesRole> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }
}
