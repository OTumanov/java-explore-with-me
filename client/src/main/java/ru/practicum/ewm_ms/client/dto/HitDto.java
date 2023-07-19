package ru.practicum.ewm_ms.client.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class HitDto {
    private Long id;
    @NotBlank
    private String app;
    @NotBlank
    private String uri;
    @NotBlank
    private String ip;
    private String timeStamp;
    @NotNull
    private Long eventId;

    public HitDto(String app, String uri, String ip, String timeStamp, Long eventId) {
        this.app = app;
        this.uri = uri;
        this.ip = ip;
        this.timeStamp = timeStamp;
        this.eventId = eventId;
    }
}