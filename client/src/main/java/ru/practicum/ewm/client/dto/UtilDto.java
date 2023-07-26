package ru.practicum.ewm.client.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
public class UtilDto {
    private Long entityId;
    private Long count;

    public UtilDto() {
    }

    public UtilDto(Long entityId, Long count) {
        this.entityId = entityId;
        this.count = count;
    }
}
