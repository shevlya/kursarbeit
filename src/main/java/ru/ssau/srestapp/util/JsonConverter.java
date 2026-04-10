package ru.ssau.srestapp.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.ssau.srestapp.exception.UtilException;

import java.util.Map;

// Добавлен для преобразования Map<String, Object> в JSON-строку и обратно.
@Converter
public class JsonConverter implements AttributeConverter<Map<String, Object>, String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        if (attribute == null) return null;
        try {
            return mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new UtilException("Не удалось преобразовать Map в JSON", e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return null;
        try {
            return mapper.readValue(dbData, Map.class);
        } catch (JsonProcessingException e) {
            throw new UtilException("Не удалось преобразовать JSON в Map", e);
        }
    }
}
