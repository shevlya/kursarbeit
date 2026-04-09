package ru.ssau.srestapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.ssau.srestapp.dto.event.*;
import ru.ssau.srestapp.entity.EventStatus;
import ru.ssau.srestapp.exception.*;
import ru.ssau.srestapp.service.EventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getAll() {
        return eventService.getAll();
    }

    @GetMapping("/verified")
    public List<EventShortDto> getAllVerified() {
        return eventService.getAllVerified();
    }

    @GetMapping("/active")
    public List<EventShortDto> getActiveAndVerified() {
        return eventService.getActiveAndVerified();
    }

    @GetMapping("/{id}")
    public EventResponseDto getById(@PathVariable Long id) throws EntityNotFoundException {
        return eventService.getById(id);
    }

    @GetMapping("/organizer/{organizerId}")
    public List<EventShortDto> getByOrganizer(@PathVariable Long organizerId) {
        return eventService.getByOrganizer(organizerId);
    }

    @GetMapping("/category/{categoryId}")
    public List<EventShortDto> getByCategory(@PathVariable Long categoryId) {
        return eventService.getByCategory(categoryId);
    }

    @GetMapping("/range")
    public List<EventShortDto> getByDateRange(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return eventService.getByDateRange(from, to);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponseDto create(@Valid @RequestBody EventRequestDto dto) throws EntityNotFoundException, InvalidDateTimeException {
        return eventService.create(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException {
        eventService.delete(id);
    }

    @GetMapping("/search")
    public List<EventShortDto> search(@RequestParam String keyword) {
        return eventService.search(keyword);
    }

    @GetMapping("/status/{status}")
    public List<EventShortDto> getByStatus(@PathVariable EventStatus status) {
        return eventService.getByStatus(status);
    }

    @PatchMapping("/{id}/verify")
    public EventResponseDto verify(@PathVariable Long id, @RequestParam Boolean verified, @RequestParam(required = false) Boolean sendEmail, @RequestBody(required = false) Map<String, String> body) throws EntityNotFoundException {
        String comment = body != null ? body.get("verificationComment") : null;
        return eventService.verify(id, verified, comment, sendEmail);
    }

    @PostMapping("/update-statuses")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateEventStatuses() throws EntityNotFoundException {
        eventService.updateEventStatuses();
    }

    @PutMapping("/{id}/submit")
    public EventResponseDto submitChanges(@PathVariable Long id, @Valid @RequestBody EventSubmitChangesDto eventSubmitChangesDto)
            throws EntityNotFoundException, InvalidDateTimeException, EventNotEditableException {
        return eventService.submitChanges(id, eventSubmitChangesDto);
    }

    @GetMapping("/admin/pending")
    public List<EventShortDto> getPendingEvents() {
        return eventService.getPendingEvents();
    }

    @PostMapping("/admin/{id}/approve")
    public EventResponseDto approveChanges(@PathVariable Long id, @RequestBody ApproveChangesDto approveDto) throws EntityNotFoundException, InvalidDateTimeException, ModerationException {
        return eventService.approveChanges(id, approveDto);
    }

    @PostMapping("/admin/{id}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectChanges(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) throws EntityNotFoundException {
        String comment = body != null ? body.get("comment") : null;
        eventService.rejectChanges(id, comment);
    }
}
