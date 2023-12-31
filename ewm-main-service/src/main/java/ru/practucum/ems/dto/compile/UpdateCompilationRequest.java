package ru.practucum.ems.dto.compile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class UpdateCompilationRequest {
    private List<Integer> events;
    private boolean pinned;

    @Length(min = 1, max = 50, message = "Заголовок подборки может содержать от 1 до 50 символов")
    private String title;
}