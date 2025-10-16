package stuthemp.recipes.recipes_app.dto.request.search;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CuisineDto {
    private List<String> include = new ArrayList<>();
}
