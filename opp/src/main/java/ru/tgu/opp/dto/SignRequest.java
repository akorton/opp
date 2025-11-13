package ru.tgu.opp.dto;

import lombok.Data;

@Data
public class SignRequest {
    private String username;
    private String password;
    private String role;
}
