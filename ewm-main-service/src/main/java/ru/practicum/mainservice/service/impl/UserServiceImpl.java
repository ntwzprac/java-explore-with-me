package ru.practicum.mainservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.dto.mapper.UserMapper;
import ru.practicum.mainservice.dto.request.NewUserRequest;
import ru.practicum.mainservice.dto.response.UserDto;
import ru.practicum.mainservice.exception.ConflictException;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.model.User;
import ru.practicum.mainservice.repository.UserRepository;
import ru.practicum.mainservice.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto registerUser(NewUserRequest dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email must be unique");
        }
        User user = userRepository.save(UserMapper.toEntity(dto));
        return UserMapper.toDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = getUserOrThrow(userId);
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        List<User> users;
        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAll(PageRequest.of(from / size, size)).getContent();
        } else {
            users = userRepository.findAllById(ids);
        }
        return users.stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
    }
}