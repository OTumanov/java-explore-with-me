package ru.practucum.ems.dto.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewUserDto {

    @NotEmpty
    @Email
    @Length(min = 6, max = 254, message = "Адрес почты может содержать от 6 до 254 символов")
    private String email;

    @NotEmpty
    @NotBlank
    @Length(min = 2, max = 250, message = "Имя может содержать от 2 до 250 символов")
    private String name;
}