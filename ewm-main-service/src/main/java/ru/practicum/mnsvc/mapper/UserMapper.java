package ru.practicum.mnsvc.mapper;


import ru.practicum.mnsvc.dto.users.NewUserDto;
import ru.practicum.mnsvc.dto.users.UserDto;
import ru.practicum.mnsvc.dto.users.UserShortDto;
import ru.practicum.mnsvc.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {

    private UserMapper() {
    }

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

    public static List<UserDto> toUserDtoList(List<User> users) {
        List<UserDto> result = new ArrayList<>();
        for (User u : users) {
            result.add(toUserDto(u));
        }
        return result;
    }
}