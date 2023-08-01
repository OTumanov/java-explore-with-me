package ru.practucum.ems.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practucum.ems.dto.users.NewUserDto;
import ru.practucum.ems.dto.users.UserDto;
import ru.practucum.ems.mapper.UserMapper;
import ru.practucum.ems.model.User;
import ru.practucum.ems.repository.UserRepository;
import ru.practucum.ems.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> findUsers(List<Long> ids, Integer from, Integer size) {
        List<User> users;
        if (ids != null) {
            users = userRepository.findAllById(ids);
        } else {
            Pageable pageable = PageRequest.of(from / size, size);
            users = userRepository.findAll(pageable).toList();
        }

        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto postUser(NewUserDto dto) {
        User user = UserMapper.toModel(dto);
        user = userRepository.save(user);

        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}