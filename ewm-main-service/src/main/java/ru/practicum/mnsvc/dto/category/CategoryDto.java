package ru.practicum.mnsvc.dto.category;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class CategoryDto {
    private Long id;

    @NotBlank
    private String name;
}