package ru.practicum.mnsvc.dto.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserShortDto {
    @NotEmpty
    private Long id;

    @NotEmpty
    private String name;
}