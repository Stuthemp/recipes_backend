package stuthemp.recipes.recipes_app.web.dto;

import lombok.Data;
import stuthemp.recipes.recipes_app.model.RecipesRole;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class JwtAuthenticationResponse {
    private String token;
    private String username;
    private Set<String> roles;

    public JwtAuthenticationResponse(String token, String username, Set<String> roles) {
        this.token = token;
        this.username = username;
        this.roles = roles;
    }

}