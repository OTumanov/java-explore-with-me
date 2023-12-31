package ru.practucum.ems.dto.compile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {
    private List<Integer> events;
    private Boolean pinned;

    @NotEmpty
    @NotBlank
    @Length(min = 1, max = 50, message = "Название подборки может содержать от 1 до 50 символов")
    private String title;
}
