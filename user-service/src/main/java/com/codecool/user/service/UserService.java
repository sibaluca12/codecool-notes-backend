package com.codecool.user.service;

import com.codecool.user.entity.UserEntity;
import com.codecool.user.model.UserModel;
import com.codecool.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    UserService() {
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }



    public boolean handleRegistration(String username, String email, String password) {

        boolean exists = userRepository.existsByUsernameOrEmail(username, email);
        if(exists){
            return true;
        }
        else {
            UserEntity userEntity = UserEntity.builder()
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .username(username)
                    .build();
            userRepository.save(userEntity);
        }
        return false;
    }

    public int getUserId(String username){

        return userRepository.getUserId(username);
    }

}
