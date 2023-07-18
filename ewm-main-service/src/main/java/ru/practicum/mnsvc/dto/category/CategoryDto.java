package ru.practicum.mnsvc.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    @NotBlank
    private String name;
}