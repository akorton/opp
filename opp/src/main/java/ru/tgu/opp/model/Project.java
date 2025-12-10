package ru.tgu.opp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private String title;
    private String subject;
    private String description;
    private LocalDateTime deadline;
    @ManyToMany
    private List<Executor> executors;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Task> tasks;
    @ManyToOne
    private Client client;
}
