package ru.practicum.mnsvc.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "participations")
public class Participation {

    public static final String EVENT_ID_COLUMN_NAME = "event_id";
    public static final String STATE_COLUMN_NAME = "state";
    public static final String CREATED_COLUMN_NAME = "created";
    public static final String REQUESTER_ID_COLUMN_NAME = "requester_id";
    public static final String ID_COLUMN_NAME = "participation_id";

    @Id
    @Column(name = ID_COLUMN_NAME)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = CREATED_COLUMN_NAME, nullable = false)
    private LocalDateTime created;
    @ManyToOne
    @JoinColumn(name = EVENT_ID_COLUMN_NAME)
    private Event event;
    @ManyToOne
    @JoinColumn(name = REQUESTER_ID_COLUMN_NAME)
    private User requester;
    private ParticipationState state;
}