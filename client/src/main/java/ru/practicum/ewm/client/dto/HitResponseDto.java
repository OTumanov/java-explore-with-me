package ru.practicum.ewm.client.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
public class HitResponseDto {
//    private String app;
    private String uri;
    private Long hits;

    public HitResponseDto(String uri, Long hits) {
//        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }
}