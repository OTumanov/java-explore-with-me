package ru.practicum.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class UtilDto {
    private final Long entityId;
    private final Long count;

    public UtilDto(Long entityId, Long count) {
        this.entityId = entityId;
        this.count = count;
    }
}