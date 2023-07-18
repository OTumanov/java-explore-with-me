package ru.practicum.mnsvc.dto.compile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CompilationPostDto {
    private List<Long> events;
    private Boolean pinned;
    private String title;

    @Override
    public String toString() {
        return "CompilationPostDto{" +
                "events=" + events +
                ", pinned=" + pinned +
                ", title='" + title + '\'' +
                '}';
    }
}