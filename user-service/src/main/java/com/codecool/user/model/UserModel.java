package com.codecool.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {

    private Long id;

    private String username;

    private String email;

    private String password;

    private List<String> roles = new ArrayList<>();
}
