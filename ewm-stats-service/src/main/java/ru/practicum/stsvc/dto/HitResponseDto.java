package ru.practicum.stsvc.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@Builder
public class HitResponseDto {
    //    private App app;
    private String uri;
    private Long hits;

    //    public HitResponseDto(App app, String uri, Long hits) {
    public HitResponseDto(String uri, Long hits) {
//        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }
}