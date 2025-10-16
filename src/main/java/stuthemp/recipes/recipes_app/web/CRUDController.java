package stuthemp.recipes.recipes_app.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import stuthemp.recipes.recipes_app.dto.request.creation.DishCreationDto;
import stuthemp.recipes.recipes_app.dto.request.search.DishSearchDto;
import stuthemp.recipes.recipes_app.model.Dish;
import stuthemp.recipes.recipes_app.service.DishService;

import java.util.List;

@Controller
@CrossOrigin(origins = "*")
public class CRUDController {

    @Autowired
    DishService dishService;

    @PostMapping(path = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createDish(@RequestBody DishCreationDto dto) {
        if(dishService.create(dto)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<Dish>> search(@RequestBody DishSearchDto dto) {
        return ResponseEntity.ok().body(dishService.findAll(dto));
    }

    @GetMapping(value = "/all_ingredients", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> ingredients() {
        return ResponseEntity.ok().body(dishService.findAllIngredients());
    }

    @GetMapping(value = "/all_cook_processes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> cookProcesses() {
        return ResponseEntity.ok().body(dishService.findAllCookProcesses());
    }

    @GetMapping(value = "/all_cuisines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> cuisines() {
        return ResponseEntity.ok().body(dishService.findAllCuisines());
    }

}
