package ru.practicum.mnsvc.mapper;


import ru.practicum.mnsvc.dto.users.NewUserDto;
import ru.practicum.mnsvc.dto.users.UserDto;
import ru.practicum.mnsvc.dto.users.UserShortDto;
import ru.practicum.mnsvc.model.User;

public class UserMapper {

    public static User toModel(NewUserDto dto) {
        return User.builder()
                .id(null)
                .email(dto.getEmail())
                .name(dto.getName())
                .build();
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}