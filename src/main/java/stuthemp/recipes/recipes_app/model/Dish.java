package stuthemp.recipes.recipes_app.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Set;

@Data
@Entity
@Table(name = "dishes")
@EntityListeners(AuditingEntityListener.class)
public class Dish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String url;

    @Column(nullable = true)
    private String instruction;

    @Column(nullable = false, name = "cooking_time")
    private Integer time;

    @Column(name = "preparation_needed", nullable = true)
    private Boolean preparationNeeded;

    @Column(name = "is_expensive", nullable = true)
    private Boolean isExpensive;

    @Column(name = "has_meat", nullable = true)
    private Boolean isMeaty;

    @Column(name = "has_seafood", nullable = true)
    private Boolean isSeafood;

    @Column(name = "is_spicy", nullable = true)
    private Boolean isSpicy;

    @Column(name = "is_sour", nullable = true)
    private Boolean isSour;

    @Column(name = "is_sweet", nullable = true)
    private Boolean isSweet;

    @Column(name = "is_soup", nullable = true)
    private Boolean isSoup;

    @Column(name = "is_dietary", nullable = true)
    private Boolean isDietary;

    @Column(name = "is_fat", nullable = true)
    private Boolean isFat;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "dish_ingredients",
            joinColumns = @JoinColumn(name = "dish_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    private Set<Ingredient> ingredients;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "dish_cuisines",
            joinColumns = @JoinColumn(name = "dish_id"),
            inverseJoinColumns = @JoinColumn(name = "cuisine_id"))
    private Set<Cuisine> cuisines;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "dish_cook_process",
            joinColumns = @JoinColumn(name = "dish_id"),
            inverseJoinColumns = @JoinColumn(name = "cook_process_id"))
    private Set<CookProcess> cookProcess;
}
