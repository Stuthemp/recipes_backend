package stuthemp.recipes.recipes_app.dto.request.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DishSearchDto {
    private IngredientsDto ingredients;
    @JsonProperty("cook_process")
    private CookProcessDto cookProcess;
    private CuisineDto cuisines;
    private TimeConstraints time;
    @JsonProperty("preparation_needed")
    private Boolean preparationNeeded;
    @JsonProperty("is_expensive")
    private Boolean isExpensive;
    @JsonProperty("dish_name")
    private String name;
    @JsonProperty("is_meaty")
    private Boolean isMeaty;
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
    private String instruction;
}
