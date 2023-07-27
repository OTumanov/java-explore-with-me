package ru.practicum.mnsvc.dto.users;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserShortDto {
    @NotEmpty
    private Long id;

    @NotEmpty
    private String name;
}