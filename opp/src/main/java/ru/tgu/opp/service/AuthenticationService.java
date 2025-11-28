package ru.tgu.opp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.tgu.opp.dto.JwtAuthenticationResponse;
import ru.tgu.opp.dto.LoginRequest;
import ru.tgu.opp.dto.RegisterRequest;
import ru.tgu.opp.model.Role;
import ru.tgu.opp.model.User;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationResponse signUp(RegisterRequest request) {
        Role role = Role.valueOf(request.getRole());

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userService.create(user, role);

        var jwt = jwtService.generateToken(user);

        return new JwtAuthenticationResponse(jwt);
    }

    public JwtAuthenticationResponse signIn(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        var user = userService.userDetailsService().loadUserByUsername(request.getUsername());

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }
}
