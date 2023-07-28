package ru.practicum.mnsvc.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(CompEventKey.class)
@Entity(name = "compilation_events")
public class CompEvent {
    @Id
    @Column(name = "compilation_id")
    private Long compilationId;

    @Id
    @Column(name = "event_id")
    private Long eventId;
}