package ru.practicum.mnsvc.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "compilations")
public class Compilation {

    public static final String ID_COLUMN_NAME = "compilation_id";
    public static final String PINNED_COLUMN_NAME = "pinned";
    public static final String TITLE_COLUMN_NAME = "title";

    @Id
    @Column(name = ID_COLUMN_NAME)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "compilation_events",
            joinColumns = {@JoinColumn(name = "compilation_id")},
            inverseJoinColumns = {@JoinColumn(name = "event_id")}
    )
    private List<Event> events;
    @Column(name = PINNED_COLUMN_NAME, nullable = false )
    private Boolean pinned;
    @Column(name = TITLE_COLUMN_NAME, nullable = false)
    private String title;
}