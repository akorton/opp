package ru.tgu.opp.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@SuperBuilder
@NoArgsConstructor
@Entity(name = "executors")
public class Executor extends User {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(Role.EXECUTOR.name()));
    }

    @Override
    public Role getRole() {
        return Role.EXECUTOR;
    }

    public static Executor fromUser(User user) {
        return Executor.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .id(user.getId())
                .build();
    }

}
