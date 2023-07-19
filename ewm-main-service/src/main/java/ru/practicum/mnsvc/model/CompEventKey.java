package ru.practicum.mnsvc.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
public class CompEventKey implements Serializable {
    private Long compilationId;
    private Long eventId;
}