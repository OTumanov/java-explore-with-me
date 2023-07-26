package ru.practicum.mnsvc.dto.users;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class UserDto {

    @NotEmpty
    private String email;
    private Long id;

    @NotEmpty
    private String name;
}