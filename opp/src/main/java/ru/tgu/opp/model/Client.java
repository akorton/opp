package ru.tgu.opp.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@SuperBuilder
@NoArgsConstructor
@Entity(name = "clients")
public class Client extends User {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(Role.CLIENT.name()));
    }

    @Override
    public Role getRole() {
        return Role.CLIENT;
    }

    public static Client fromUser(User user) {
        return Client.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .id(user.getId())
                .build();
    }
}
