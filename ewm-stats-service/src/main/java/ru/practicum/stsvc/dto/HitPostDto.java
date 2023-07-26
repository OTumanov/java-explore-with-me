package ru.practicum.stsvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class HitPostDto {

    private Long id;
    //    @NotBlank
//    private String app;
    private String uri;
    private String ip;
    private String timeStamp;
    private Long eventId;
}