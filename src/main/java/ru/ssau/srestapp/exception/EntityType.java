package ru.ssau.srestapp.exception;

public enum EntityType {
    USER("Пользователь"),
    EVENT("Мероприятие"),
    CATEGORY("Категория"),
    ROLE("Роль"),
    PLACE("Место"),
    ONLINE_PLACE("Онлайн-место"),
    PHYSICAL_PLACE("Физическое место"),
    AVATAR("Аватар"),
    ORGANIZER_REQUEST("Заявка организатора"),
    USER_INTEREST("Интерес");

    private final String russianName;

    EntityType(String russianName) {
        this.russianName = russianName;
    }

    public String notFound(Long id) {
        return String.format("%s с id=%d не найден", russianName, id);
    }

    public String notFound(String identifier) {
        return String.format("%s с идентификатором «%s» не найден", russianName, identifier);
    }

    public String duplicate(String name) {
        return String.format("%s с названием «%s» уже существует", russianName, name);
    }

    public String notFoundByEmail(String email) {
        return String.format("%s с email «%s» не найден", russianName, email);
    }

    public String duplicateEmail(String email) {
        return String.format("%s с email «%s» уже существует", russianName, email);
    }

    public String notFoundFeminine(Long id) {
        return String.format("%s с id=%d не найдена", russianName, id);
    }

    public String notFoundNeuter(Long id) {
        return String.format("%s с id=%d не найдено", russianName, id);
    }
}
