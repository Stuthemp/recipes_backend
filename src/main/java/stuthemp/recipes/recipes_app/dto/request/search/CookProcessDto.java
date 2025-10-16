package stuthemp.recipes.recipes_app.dto.request.search;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CookProcessDto {

    private List<String> include = new ArrayList<>();
    private List<String> exclude = new ArrayList<>();

}
