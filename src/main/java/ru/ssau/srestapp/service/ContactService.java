package ru.ssau.srestapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.ssau.srestapp.dto.email.ContactMessageRequestDto;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {

    private final EmailService emailService;

    @Value("${email.from.address:dashashevlyak@mail.ru}")
    private String adminEmail;

    public void sendContactMessage(ContactMessageRequestDto dto) {
        Map<String, Object> adminVars = new HashMap<>();
        adminVars.put("senderName", dto.getName());
        adminVars.put("senderEmail", dto.getEmail());
        adminVars.put("subject", dto.getSubject());
        adminVars.put("messageText", dto.getMessage());
        emailService.sendContactMessageToAdmin(adminEmail, adminVars);

        Map<String, Object> userVars = new HashMap<>();
        userVars.put("userName", dto.getName());
        userVars.put("subject", dto.getSubject());
        emailService.sendContactConfirmationToUser(dto.getEmail(), userVars);

        log.info("Обращение от {} <{}> отправлено администратору", dto.getName(), dto.getEmail());
    }
}
