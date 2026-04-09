package ru.ssau.srestapp.dto.event;

import lombok.Data;

import java.util.List;

//какие поля изменяем от админа инфа
@Data
public class ApproveChangesDto {
    private List<String> fields;   // какие поля применить (если null или пустой — отклонить всё)
    private Boolean applyAll;      // если true, игнорируем fields и применяем все изменения
}
