package ru.practicum.mnsvc.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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