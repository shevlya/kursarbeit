package ru.ssau.srestapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.srestapp.dto.place.PlaceResponseDto;
import ru.ssau.srestapp.entity.OnlinePlace;
import ru.ssau.srestapp.entity.PhysicalPlace;
import ru.ssau.srestapp.entity.Place;
import ru.ssau.srestapp.repository.PlaceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private static final String TYPE_PHYSICAL = "PHYSICAL";
    private static final String TYPE_ONLINE = "ONLINE";
    private static final String TYPE_UNKNOWN = "UNKNOWN";

    private final PlaceRepository placeRepository;

    @Transactional(readOnly = true)
    public List<PlaceResponseDto> getAll() {
        return placeRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    private PlaceResponseDto toDto(Place place) {
        String type = determinePlaceType(place);
        return new PlaceResponseDto(
                place.getIdPlace(),
                place.getPlaceName(),
                place.getPlaceDescription(),
                type
        );
    }

    private String determinePlaceType(Place place) {
        if (place instanceof PhysicalPlace) {
            return TYPE_PHYSICAL;
        } else if (place instanceof OnlinePlace) {
            return TYPE_ONLINE;
        } else {
            return TYPE_UNKNOWN;
        }
    }
}
