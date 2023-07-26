package ru.practicum.ewm.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HitPostDto {

    private Long id;
//    private String app;
    private String uri;
    private String ip;
    private String timeStamp;
    private Long eventId;

}