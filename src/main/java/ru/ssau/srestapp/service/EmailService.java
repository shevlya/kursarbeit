package ru.ssau.srestapp.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ru.ssau.srestapp.dto.email.EmailTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${email.from.address:dashashevlyak@mail.ru}")
    private String fromAddress;

    @Value("${email.from.name:Freizeitgestaltung}")
    private String fromName;

    @Value("${email.base-url:http://localhost:4200}")
    private String baseUrl;

    @Value("${email.color.primary:#7C3AED}")
    private String colorPrimary;

    @Value("${email.color.secondary:#A78BFA}")
    private String colorSecondary;

    @Value("${email.color.background:#F5F3FF}")
    private String colorBackground;

    @Value("${email.color.text:#1F2937}")
    private String colorText;

    @Async
    public void sendEmail(String to, EmailTemplate emailTemplate, Map<String, Object> variables) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress, fromName);
            helper.setTo(to);
            helper.setSubject(emailTemplate.getDefaultSubject());
            String htmlContent = processTemplate(emailTemplate.getFileName(), variables);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Email отправлен: {} (шаблон: {})", to, emailTemplate.name());
        } catch (MessagingException e) {
            log.error("Ошибка отправки email на {}: {}", to, e.getMessage());
        } catch (Exception e) {
            log.error("Критическая ошибка отправки email: {}", e.getMessage());
        }
    }

    //Обработка HTML шаблона через Thymeleaf
    private String processTemplate(String templateName, Object variables) {
        Context context = new Context();
        context.setVariable("baseUrl", baseUrl);
        context.setVariable("colorPrimary", colorPrimary);
        context.setVariable("colorSecondary", colorSecondary);
        context.setVariable("colorBackground", colorBackground);
        context.setVariable("colorText", colorText);
        if (variables instanceof Map<?, ?> map) {
            map.forEach((k, v) -> context.setVariable(k.toString(), v));
        }
        return templateEngine.process("email/" + templateName, context);
    }

    public void sendEventApproved(String to, String eventName, String comment) {
        Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("eventName", eventName);
        variables.put("comment", comment != null ? comment : "Мероприятие успешно прошло модерацию");
        variables.put("actionUrl", baseUrl + "/events");
        sendEmail(to, EmailTemplate.EVENT_APPROVED, variables);
    }

    public void sendEventRejected(String to, String eventName, String comment) {
        Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("eventName", eventName);
        variables.put("comment", comment != null ? comment : "Мероприятие не прошло модерацию");
        variables.put("actionUrl", baseUrl + "/events/create");
        sendEmail(to, EmailTemplate.EVENT_REJECTED, variables);
    }

    /*
    public void sendEventEdited(String to, String eventName, String comment, boolean notify) {
        if (!notify) return;
        Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("eventName", eventName);
        variables.put("comment", comment != null ? comment : "Администратор внёс изменения");
        variables.put("actionUrl", baseUrl + "/events");
        sendEmail(to, EmailTemplate.EVENT_EDITED, variables);
    }*/

    public void sendParticipationConfirmed(String to, String userName, String eventName, String eventDate, String placeName) {
        Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("userName", userName);
        variables.put("eventName", eventName);
        variables.put("eventDate", eventDate);
        variables.put("placeName", placeName);
        variables.put("actionUrl", baseUrl + "/events");
        sendEmail(to, EmailTemplate.PARTICIPATION_CONFIRMED, variables);
    }

    public void sendParticipationCancelled(String to, String userName, String eventName, String eventDate) {
        Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("userName", userName);
        variables.put("eventName", eventName);
        variables.put("eventDate", eventDate);
        sendEmail(to, EmailTemplate.PARTICIPATION_CANCELLED, variables);
    }

    public void sendParticipationRejected(String to, String userName, String eventName, String comment) {
        Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("userName", userName);
        variables.put("eventName", eventName);
        variables.put("comment", comment != null ? comment : "Организатор отклонил вашу заявку");
        sendEmail(to, EmailTemplate.PARTICIPATION_REJECTED, variables);
    }

    public void sendWaitlistPromoted(String to, String userName, String eventName, String eventDate) {
        Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("userName", userName);
        variables.put("eventName", eventName);
        variables.put("eventDate", eventDate);
        variables.put("actionUrl", baseUrl + "/events");
        sendEmail(to, EmailTemplate.WAITLIST_PROMOTED, variables);
    }

    public void sendWelcome(String to, String userName) {
        Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("userName", userName);
        variables.put("actionUrl", baseUrl + "/events");
        sendEmail(to, EmailTemplate.WELCOME, variables);
    }

    public void sendParticipationAttended(String to, String userName, String eventName, String eventDate) {
        Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("userName", userName);
        variables.put("eventName", eventName);
        variables.put("eventDate", eventDate);
        variables.put("actionUrl", baseUrl + "/events");
        sendEmail(to, EmailTemplate.PARTICIPATION_ATTENDED, variables);
    }

    public void sendOrganizerRequestApproved(String to, String userName) {
        Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("userName", userName);
        variables.put("actionUrl", baseUrl + "/events/create");
        sendEmail(to, EmailTemplate.ORGANIZER_REQUEST_APPROVED, variables);
    }

    public void sendOrganizerRequestRejected(String to, String userName, String comment) {
        Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("userName", userName);
        variables.put("comment", comment != null ? comment : "Ваша заявка не прошла модерацию");
        sendEmail(to, EmailTemplate.ORGANIZER_REQUEST_REJECTED, variables);
    }

    public void sendEventChangesApproved(String to, String eventName, List<String> acceptedFields, String comment) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("eventName", eventName);
        variables.put("comment", comment != null ? comment : "Ваши изменения приняты");
        variables.put("acceptedFields", acceptedFields); // список принятых полей
        variables.put("actionUrl", baseUrl + "/events");
        sendEmail(to, EmailTemplate.EVENT_CHANGES_APPROVED, variables);
    }

    public void sendEventChangesRejected(String to, String eventName, List<String> rejectedFields, String comment) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("eventName", eventName);
        variables.put("comment", comment != null ? comment : "Ваши изменения отклонены");
        variables.put("rejectedFields", rejectedFields);
        variables.put("actionUrl", baseUrl + "/events");
        sendEmail(to, EmailTemplate.EVENT_CHANGES_REJECTED, variables);
    }
}
