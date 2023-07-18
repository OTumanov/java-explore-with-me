package ru.practicum.mnsvc.service;


import ru.practicum.mnsvc.dto.users.NewUserDto;
import ru.practicum.mnsvc.dto.users.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findUsers(List<Long> ids, Integer from, Integer size);

    UserDto postUser(NewUserDto dto);

    void deleteUser(Long userId);
}