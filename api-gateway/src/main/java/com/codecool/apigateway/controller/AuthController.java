package com.codecool.apigateway.controller;



import com.codecool.apigateway.AppProperties;
import com.codecool.apigateway.entity.Session;
import com.codecool.apigateway.model.UserCredentials;
import com.codecool.apigateway.model.UserData;
import com.codecool.apigateway.security.JwtTokenServices;
import com.codecool.apigateway.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenServices jwtTokenServices;

    private final AppProperties appProperties;

    private final TokenService tokenService;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenServices jwtTokenServices,  TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenServices = jwtTokenServices;
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();;
        this.tokenService = tokenService;
    }

    @Value("${user-service.url}")
    private String baseUrl;


    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity login(@RequestBody UserCredentials data) {
        String username = data.getUsername();
        UserData appUserRecord = restTemplate.getForObject( baseUrl +"/getUser/" + username, UserData.class);
//        AppUserRecord appUserRecord = this.dsl.selectFrom(APP_USER)
//                .where(APP_USER.EMAIL.eq(username)).fetchOne();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
           List<String> roles = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

        if (appUserRecord != null) {
            boolean pwMatches = this.passwordEncoder.matches(password,
                    appUserRecord.getPasswordHash());
            if (pwMatches && appUserRecord.getEnabled().booleanValue()) {

                String sessionId = jwtTokenServices.createToken(username, roles);

                Session.builder()
                        .id(sessionId)
                        .user_id(data.getId())
                        .timestamp(LocalDate.now().plus(this.appProperties.getCookieMaxAge()))
                        .build();


//                AppSessionRecord record = this.dsl.newRecord(APP_SESSION);
//                record.setId(sessionId);
//                record.setAppUserId(appUserRecord.getId());
//                record.setValidUntil(LocalDateTime.now().plus(this.appProperties.getCookieMaxAge()));
//                record.store();

                ResponseCookie cookie = ResponseCookie
                        .from(AuthCookieFilter.COOKIE_NAME, sessionId)
                        .maxAge(this.appProperties.getCookieMaxAge()).sameSite("Strict")
                        .path("/").httpOnly(true).secure(this.appProperties.isSecureCookie()).build();

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .body(appUserRecord.getAuthority());
            }
        }
        else {
            this.passwordEncoder.matches(password, this.userNotFoundEncodedPassword);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/authenticate")
    @PreAuthorize("isFullyAuthenticated()")
    public String authenticate(@AuthenticationPrincipal AppUserDetail user) {
        return user.getAuthorities().iterator().next().getAuthority();
    }


//
//    @ResponseStatus(HttpStatus.OK)
//    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity login(@RequestBody UserCredentials data) {
//        try {

//            String username = data.getUsername();
//
//            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
//            List<String> roles = authentication.getAuthorities()
//                    .stream()
//                    .map(GrantedAuthority::getAuthority)
//                    .collect(Collectors.toList());
//
//            String token = jwtTokenServices.createToken(username, roles);
//            UserData user = restTemplate.getForObject( baseUrl +"/getUser/" + username, UserData.class);
//
//            Map<Object, Object> model = new HashMap<>();
//            model.put("username", username);
//            model.put("userid", user.getId());
//            model.put("email", user.getEmail());
//            model.put("roles", roles);
//            model.put("token", token);
//            return ResponseEntity.ok(model);
//        } catch (AuthenticationException e) {
//            throw new BadCredentialsException("Invalid username/password supplied");
//        }
//    }


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