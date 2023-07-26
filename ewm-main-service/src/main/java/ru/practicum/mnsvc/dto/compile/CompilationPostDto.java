package ru.practicum.mnsvc.dto.compile;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class CompilationPostDto {
    private List<Long> events;
    private Boolean pinned;
    private String title;
}