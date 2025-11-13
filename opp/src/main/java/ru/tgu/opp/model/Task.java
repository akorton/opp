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
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private String description;
    private LocalDateTime deadline;
    private TaskStatus status;

    @ManyToMany
    @JoinTable(
            name = "task_prerequisites",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "prerequisite_id")
    )
    private List<Task> prerequisites;
    @ManyToMany(mappedBy = "prerequisites")
    private List<Task> prerequisitesOf;

    @ManyToMany
    private List<Executor> executors;
}
