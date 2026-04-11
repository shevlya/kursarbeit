package ru.ssau.srestapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.ssau.srestapp.dto.email.ContactMessageRequestDto;
import ru.ssau.srestapp.service.ContactService;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping("/send")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void sendMessage(@Valid @RequestBody ContactMessageRequestDto dto) {
        contactService.sendContactMessage(dto);
    }
}
