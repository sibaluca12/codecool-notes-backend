package com.codecool.user.controller;


import com.codecool.user.entity.UserEntity;
import com.codecool.user.model.UserModel;
import com.codecool.user.repository.UserRepository;
import com.codecool.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/registration", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> doRegistration(@RequestBody UserModel userModel) {
        boolean userExists = userService.handleRegistration(userModel.getUsername(), userModel.getEmail(), userModel.getPassword());
        if(userExists){
            return ResponseEntity.ok(-1);
        }
        int userId = userService.getUserId(userModel.getUsername());
        return ResponseEntity.ok(userId);
    }

    @PostMapping(value = "/changePassword", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean changePassword(@RequestBody UserModel userModel){
        boolean exists = userRepository.existsByUsernameOrEmail(userModel.getUsername(), userModel.getEmail());
        if(exists){
            userRepository.updatePassword(userModel.getPassword(), userModel.getUsername());
            return true;
        } else {
            return false;
        }
    }

    @PostMapping(value = "/changeEmail", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean changeEmail(@RequestBody UserModel userModel){
        boolean exists = userRepository.existsByUsernameOrEmail(userModel.getUsername(), userModel.getEmail());
        if(exists){
            userRepository.updateEmail(userModel.getEmail(), userModel.getUsername());
            return true;
        } else {
            return false;
        }
    }



//
//    @Autowired
//    private UserRepository userRepository;


    @RequestMapping(path = "/getUser/{username}",method = GET)
    public UserEntity getUser(@PathVariable String username) {
        return userRepository.findByUsername(username);
    }


}
