package ru.ssau.srestapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.ssau.srestapp.dto.eventParticipant.*;
import ru.ssau.srestapp.entity.ParticipationStatus;
import ru.ssau.srestapp.exception.*;
import ru.ssau.srestapp.service.EventParticipantService;

import java.util.List;

@RestController
@RequestMapping("/api/event-participants")
@RequiredArgsConstructor
public class EventParticipantController {

    private final EventParticipantService eventParticipantService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public EventParticipantResponseDto register(@Valid @RequestBody EventParticipantRequestDto dto) throws EntityNotFoundException, ParticipantAlreadyExistsException {
        return eventParticipantService.register(dto.getUserId(), dto.getEventId());
    }

    @PostMapping("/{userId}/events/{eventId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public EventParticipantResponseDto cancelParticipation(@PathVariable Long userId, @PathVariable Long eventId, @RequestParam(required = false, defaultValue = "false") Boolean sendEmail) throws EntityNotFoundException, ParticipantNotFoundException {
        return eventParticipantService.cancelParticipation(userId, eventId, sendEmail);
    }

    @DeleteMapping("/events/{eventId}/participants/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeParticipant(@PathVariable Long userId, @PathVariable Long eventId, @RequestParam(required = false, defaultValue = "false") Boolean sendEmail) throws EntityNotFoundException, ParticipantNotFoundException {
        eventParticipantService.removeParticipant(userId, eventId, sendEmail);
    }

    @PatchMapping("/events/{eventId}/participants/{userId}/status")
    @ResponseStatus(HttpStatus.OK)
    public EventParticipantResponseDto changeStatus(@PathVariable Long userId, @PathVariable Long eventId, @RequestParam ParticipationStatus newStatus, @RequestParam(required = false, defaultValue = "false") Boolean sendEmail) throws EntityNotFoundException, ParticipantNotFoundException {
        return eventParticipantService.changeStatus(userId, eventId, newStatus, sendEmail);
    }

    @GetMapping("/events/{eventId}")
    public List<EventParticipantShortDto> getParticipantsByEvent(@PathVariable Long eventId) {
        return eventParticipantService.getParticipantsByEvent(eventId);
    }

    @GetMapping("/events/{eventId}/registered")
    public List<EventParticipantShortDto> getRegisteredParticipants(@PathVariable Long eventId) {
        return eventParticipantService.getRegisteredParticipants(eventId);
    }

    @GetMapping("/events/{eventId}/waitlisted")
    public List<EventParticipantShortDto> getWaitlistedParticipants(@PathVariable Long eventId) {
        return eventParticipantService.getWaitlistedParticipants(eventId);
    }

    @GetMapping("/users/{userId}")
    public List<EventParticipantShortDto> getEventsByUser(@PathVariable Long userId) {
        return eventParticipantService.getEventsByUser(userId);
    }

    @GetMapping("/events/{eventId}/participants/{userId}")
    public EventParticipantResponseDto getParticipant(@PathVariable Long userId, @PathVariable Long eventId) throws EntityNotFoundException, ParticipantNotFoundException {
        return eventParticipantService.getParticipant(userId, eventId);
    }

    @DeleteMapping("/admin/events/{eventId}/participants/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteParticipant(@PathVariable Long userId, @PathVariable Long eventId) throws EntityNotFoundException, ParticipantNotFoundException {
        eventParticipantService.deleteParticipant(userId, eventId);
    }
}
