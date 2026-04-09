package ru.ssau.srestapp.entity;

public enum ModerationStatus {
    PUBLISHED,      // опубликовано, изменений на модерации нет
    PENDING,        // есть предложенные изменения, ждут модерации
    REJECTED        // предложенные изменения отклонены
}
