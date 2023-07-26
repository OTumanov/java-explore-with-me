package ru.practicum.mnsvc.dto.category;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    private Long id;

    @NotBlank
    @Length(min = 1, max = 50, message = "Допустимо от 1 до 50 символов")
    private String name;
}