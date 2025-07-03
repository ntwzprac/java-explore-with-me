package ru.practicum.mainservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.request.NewUserRequest;
import ru.practicum.mainservice.dto.response.UserDto;
import ru.practicum.mainservice.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto registerUser(@RequestBody @Valid NewUserRequest dto) {
        log.info("Registering new user: {}", dto.getEmail());
        UserDto result = userService.registerUser(dto);
        log.info("User successfully registered with id: {}", result.getId());
        return result;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.info("Deleting user with id: {}", userId);
        userService.deleteUser(userId);
        log.info("User with id: {} successfully deleted", userId);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @RequestParam(defaultValue = "0") int from,
                                  @RequestParam(defaultValue = "10") int size) {
        log.info("Getting users with parameters: ids={}, from={}, size={}", ids, from, size);
        List<UserDto> users = userService.getUsers(ids, from, size);
        log.info("Found {} users", users.size());
        return users;
    }
}