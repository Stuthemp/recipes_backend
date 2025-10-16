package stuthemp.recipes.recipes_app.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import stuthemp.recipes.recipes_app.model.RecipesRole;
import stuthemp.recipes.recipes_app.model.RecipesUser;
import stuthemp.recipes.recipes_app.repository.UserRepository;
import stuthemp.recipes.recipes_app.security.JwtTokenProvider;
import stuthemp.recipes.recipes_app.web.dto.JwtAuthenticationResponse;
import stuthemp.recipes.recipes_app.web.dto.LoginRequest;
import stuthemp.recipes.recipes_app.web.dto.SignUpRequest;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 👇 НЕ ПРИВОДИМ к RecipesUser — получаем UserDetails
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // 👇 Генерируем токен на основе username и ролей из UserDetails
            String jwt = tokenProvider.generateToken(userDetails);

            // 👇 Если нужно вернуть роли — они уже есть в userDetails.getAuthorities()
            Set<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            return ResponseEntity.ok(new JwtAuthenticationResponse(
                    jwt,
                    userDetails.getUsername(),
                    roles
            ));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        if (userRepository.findByUsername(signUpRequest.getUsername()).isPresent()) {
            return new ResponseEntity<>("Username is taken!", HttpStatus.BAD_REQUEST);
        }

        RecipesUser user = new RecipesUser(
                signUpRequest.getUsername(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                Set.of(RecipesRole.ROLE_USER)
        );

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }
}
