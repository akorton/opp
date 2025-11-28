package ru.tgu.opp.dto;

import lombok.Data;
import ru.tgu.opp.model.Role;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private Role role;
}
