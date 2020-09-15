package com.codecool.apigateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserData {

    private Long id;

    private String username;

    private String password;

    // roles of the user (ADMIN, USER,..)
    @Builder.Default
    private List<String> roles = new ArrayList<>();
}