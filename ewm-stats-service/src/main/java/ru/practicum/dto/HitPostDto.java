package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
public class HitPostDto {

    private Long id;

    @NotBlank
    private String app;

    @NotBlank
    private String uri;

    @NotBlank
    @Length(min = 7, max = 15)
    private String ip;

    private String timeStamp;
    private Long eventId;
}