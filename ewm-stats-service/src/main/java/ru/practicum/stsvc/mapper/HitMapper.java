package ru.practicum.stsvc.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.ewm.client.dto.HitPostDto;
import ru.practicum.ewm.client.dto.HitResponseDto;
import ru.practicum.stsvc.model.Hit;

import java.time.LocalDateTime;

@NoArgsConstructor
public final class HitMapper {

    //    public static Hit toModel(HitPostDto dto, App app) {
    public static Hit toModel(HitPostDto dto) {
        Hit hit = Hit.builder()
                .hitId(dto.getId())
//                .app(app)
                .uri(dto.getUri())
                .ip(dto.getIp())
                .eventId(dto.getEventId())
                .build();
        if (dto.getTimeStamp() != null) {
            hit.setTimeStamp(DateTimeMapper.toDateTime(dto.getTimeStamp()));
        } else {
            hit.setTimeStamp(LocalDateTime.now());
        }
        return hit;
    }

    public static HitResponseDto toDto(Hit hit, Long countHits) {
        return HitResponseDto.builder()
//                .app(hit.getApp())
                .uri(hit.getUri())
                .hits(countHits)
                .build();
    }
}