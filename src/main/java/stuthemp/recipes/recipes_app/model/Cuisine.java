package stuthemp.recipes.recipes_app.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cuisine")
public class Cuisine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

}
