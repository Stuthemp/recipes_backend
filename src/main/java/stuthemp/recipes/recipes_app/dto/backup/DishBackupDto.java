package stuthemp.recipes.recipes_app.dto.backup;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DishBackupDto {
    List<String> ingredients;
    @JsonProperty("cook_process")
    List<String> cookProcess;
    @JsonProperty("food_type")
    String foodType;
    String time;
    String url;
    @JsonProperty("preparationNeeded")
    String preparationNeeded;
    @JsonProperty("is_expensive")
    Boolean isExpensive;
    @JsonProperty("dish_name")
    String name;
    @JsonProperty("has_meat")
    String hasMeat;
    String instruction;
}
