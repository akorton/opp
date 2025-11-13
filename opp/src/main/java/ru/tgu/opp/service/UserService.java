package ru.tgu.opp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.tgu.opp.model.Client;
import ru.tgu.opp.model.Executor;
import ru.tgu.opp.model.Role;
import ru.tgu.opp.model.User;
import ru.tgu.opp.repository.ClientRepository;
import ru.tgu.opp.repository.ExecutorRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final ClientRepository clientRepository;
    private final ExecutorRepository executorRepository;

    public User getByUsername(String username) {
        Optional<Client> clientOptional = clientRepository.findByUsername(username);
        Optional<Executor> executorOptional = executorRepository.findByUsername(username);

        if (clientOptional.isEmpty() && executorOptional.isEmpty()) {
            throw new RuntimeException("No such user.");
        }

        return clientOptional.isPresent() ? clientOptional.get() : executorOptional.get();
    }

    public Client getClientById(Integer id) {
        return clientRepository.getReferenceById(id);
    }
    public Executor getExecutorById(Integer id) {
        return executorRepository.getReferenceById(id);
    }

    public User save(User user, Role role) {
        return switch (role) {
            case EXECUTOR -> executorRepository.save(Executor.fromUser(user));
            case CLIENT -> clientRepository.save(Client.fromUser(user));
        };
    }

    public User create(User user, Role role) {
        // It is not a mistake 'username' should be unique across all users
        if (clientRepository.existsByUsername(user.getUsername()) ||
            executorRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("User with such username already exists.");
        }

        return save(user, role);
    }

    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }

    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }
}
