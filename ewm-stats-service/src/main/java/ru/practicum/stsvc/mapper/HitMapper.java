package ru.practicum.stsvc.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.ewm.client.dto.HitPostDto;
import ru.practicum.stsvc.model.Hit;

import java.time.LocalDateTime;

@NoArgsConstructor
public final class HitMapper {
    public static Hit toModel(HitPostDto dto) {
        Hit hit = Hit.builder()
                .hitId(dto.getId())
                .uri(dto.getUri())
                .ip(dto.getIp())
                .eventId(dto.getEventId())
                .app(dto.getApp())
                .build();
        if (dto.getTimeStamp() != null) {
            hit.setTimeStamp(DateTimeMapper.toDateTime(dto.getTimeStamp()));
        } else {
            hit.setTimeStamp(LocalDateTime.now());
        }
        return hit;
    }
}