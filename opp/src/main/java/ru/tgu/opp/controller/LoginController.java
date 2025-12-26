package ru.tgu.opp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.tgu.opp.dto.JwtAuthenticationResponse;
import ru.tgu.opp.dto.LoginRequest;
import ru.tgu.opp.dto.RegisterRequest;
import ru.tgu.opp.service.AuthenticationService;
import ru.tgu.opp.service.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/register")
    public JwtAuthenticationResponse signUp(@RequestBody RegisterRequest request) {
        return authenticationService.signUp(request);
    }

    @PostMapping("/login")
    public JwtAuthenticationResponse signIn(@RequestBody LoginRequest request) {
        return authenticationService.signIn(request);
    }

    @GetMapping("/test/e")
    @PreAuthorize("hasAuthority('EXECUTOR')")
    public String testE() {
        return "Success";
    }

    @GetMapping("/test/c")
    @PreAuthorize("hasAuthority('CLIENT')")
    public String testC() {
        return "Success";
    }

    @GetMapping("id")
    public Integer getId() {
        return userService.getCurrentUser().getId();
    }
}
