package ru.ssau.srestapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.ssau.srestapp.dto.user.UserRequestDto;
import ru.ssau.srestapp.dto.user.UserResponseDto;
import ru.ssau.srestapp.exception.EntityNotFoundException;
import ru.ssau.srestapp.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto register(@Valid @RequestBody UserRequestDto dto) throws EntityNotFoundException {
        return userService.create(dto);
    }
}
