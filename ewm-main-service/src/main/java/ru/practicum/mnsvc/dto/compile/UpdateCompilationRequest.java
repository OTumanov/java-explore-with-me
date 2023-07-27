package ru.practicum.mnsvc.dto.compile;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class UpdateCompilationRequest {
    private List<Integer> events;
    @Getter
    private boolean pinned;

    @Length(min = 1, max = 50, message = "Заголовок подборки должен быть от 1 до 50 символов")
    private String title;
}