package ru.ssau.srestapp.dto.email;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailTemplate {
    EVENT_APPROVED("event_approved.html", "Мероприятие одобрено"),
    EVENT_REJECTED("event_rejected.html", "Мероприятие отклонено"),
    EVENT_EDITED("event_edited.html", "Мероприятие отредактировано"),
    PARTICIPATION_CONFIRMED("participation_confirmed.html", "Участие подтверждено"),
    PARTICIPATION_CANCELLED("participation_cancelled.html", "Участие отменено"),
    PARTICIPATION_REJECTED("participation_rejected.html", "Участие отклонено организатором"),
    PARTICIPATION_ATTENDED("participation_attended.html", "Вы посетили мероприятие"),
    WAITLIST_PROMOTED("waitlist_promoted.html", "Вы переведены в участники"),
    WELCOME("welcome.html", "Добро пожаловать!"),
    ORGANIZER_REQUEST_APPROVED("organizer_request_approved.html", "Заявка одобрена. Теперь Вы организатор мероприятий."),
    ORGANIZER_REQUEST_REJECTED("organizer_request_rejected.html", "Заявка отклонена"),
    EVENT_CHANGES_APPROVED("event_changes_approved.html", "Изменения мероприятия одобрены"),
    EVENT_CHANGES_REJECTED("event_changes_rejected.html", "Изменения мероприятия отклонены"),
    CONTACT_MESSAGE_ADMIN("contact_message_admin.html", "Новое обращение от пользователя"),
    CONTACT_CONFIRMATION_USER("contact_confirmation_user.html", "Ваше обращение принято");

    private final String fileName;
    private final String defaultSubject;
}
