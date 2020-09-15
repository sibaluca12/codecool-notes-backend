package com.codecool.user;

import com.codecool.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class DataInitializer implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    DataInitializer() {
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Override
    public void run(String... args) throws Exception {

    }
}
