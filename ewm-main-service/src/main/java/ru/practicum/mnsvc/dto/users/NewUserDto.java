package ru.practicum.mnsvc.dto.users;

import lombok.*;
import ru.practicum.mnsvc.utils.CommonValidMarker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class NewUserDto {

    @Email(groups = CommonValidMarker.class)
    @NotNull(groups = CommonValidMarker.class)
    private String email;

    @NotBlank(groups = CommonValidMarker.class)
    private String name;

    @Override
    public String toString() {
        return "NewUserDto{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}