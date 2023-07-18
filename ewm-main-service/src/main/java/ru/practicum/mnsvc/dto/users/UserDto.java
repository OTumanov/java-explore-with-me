package ru.practicum.mnsvc.dto.users;

import lombok.*;

import javax.validation.constraints.Email;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class UserDto {
    private String email;
    private Long id;
    private String name;
}