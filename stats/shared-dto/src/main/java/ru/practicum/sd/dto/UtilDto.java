package ru.practicum.sd.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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