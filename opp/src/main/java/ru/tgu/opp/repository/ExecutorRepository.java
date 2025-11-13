package ru.tgu.opp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tgu.opp.model.Executor;

import java.util.Optional;

@Repository
public interface ExecutorRepository extends JpaRepository<Executor, Integer> {

    Optional<Executor> findByUsername(String username);
    Boolean existsByUsername(String username);
}
