package stuthemp.recipes.recipes_app.dto.request.creation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Set;

@Data
public class DishCreationDto {

    @JsonProperty("dish_name")
    private String name;
    private String url;
    private String instruction;
    private Integer time;
    @JsonProperty("preparation_needed")
    private Boolean preparationNeeded;
    @JsonProperty("is_expensive")
    private Boolean isExpensive;
    @JsonProperty("is_meaty")
    private Boolean isMeaty;
    @JsonProperty("is_spicy")
    private Boolean isSpicy;
    @JsonProperty("is_seafood")
    private Boolean isSeafood;
    @JsonProperty("is_sour")
    private Boolean isSour;
    @JsonProperty("is_sweet")
    private Boolean isSweet;
    @JsonProperty("is_soup")
    private Boolean isSoup;
    @JsonProperty("is_dietary")
    private Boolean isDietary;
    @JsonProperty("is_fat")
    private Boolean isFat;
    private Set<String> ingredients;
    @JsonProperty("cook_process")
    private Set<String> cookProcess;
    private Set<String> cuisines;

}
