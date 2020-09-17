package com.codecool.apigateway.controller;



import com.codecool.apigateway.model.UserCredentials;
import com.codecool.apigateway.model.UserData;
import com.codecool.apigateway.security.JwtTokenServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping(value = "/auth", method = {RequestMethod.GET, RequestMethod.POST})
public class AuthController {

    @Autowired
    RestTemplate restTemplate;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenServices jwtTokenServices;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenServices jwtTokenServices /*, UserRepository users */) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenServices = jwtTokenServices;
    }

    @Value("${user-service.url}")
    private String baseUrl;


    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity login(@RequestBody UserCredentials data) {
        try {
            String username = data.getUsername();
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
            List<String> roles = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            System.out.println("HELLOOOOO");
            String token = jwtTokenServices.createToken(username, roles);
            System.out.println("Token: "+ token);
            UserData user = restTemplate.getForObject( "http://localhost:8091/user/getUser/" + username, UserData.class);



            ResponseCookie cookie = ResponseCookie
                    .from("authentication", token)
                    .maxAge(7 * 24 * 60 * 60)  //10 hrs
                    .path("/").httpOnly(false).secure(false).build();



            Map<Object, Object> model = new HashMap<>();
            model.put("username", username);
            model.put("userid", user.getId());
            model.put("email", user.getEmail());
            model.put("roles", roles);
           // model.put("token", cookie);
            System.out.println(cookie.toString());

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(model);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied");
        }
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}