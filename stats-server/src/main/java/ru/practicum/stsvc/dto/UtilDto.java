package ru.practicum.stsvc.dto;

import lombok.ToString;

@ToString
public class UtilDto {
    private final Long entityId;
    private final Long count;

    public UtilDto(Long entityId, Long count) {
        this.entityId = entityId;
        this.count = count;
    }
}