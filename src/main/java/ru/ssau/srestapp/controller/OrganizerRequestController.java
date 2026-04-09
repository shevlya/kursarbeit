package ru.ssau.srestapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.ssau.srestapp.dto.organizerRequest.*;
import ru.ssau.srestapp.exception.EntityNotFoundException;
import ru.ssau.srestapp.exception.DuplicateEntityException;
import ru.ssau.srestapp.service.OrganizerRequestService;

import java.util.List;

@RestController
@RequestMapping("/api/organizer-requests")
@RequiredArgsConstructor
public class OrganizerRequestController {

    private final OrganizerRequestService organizerRequestService;

    @PostMapping("/me")
    @ResponseStatus(HttpStatus.CREATED)
    public OrganizerRequestResponseDto createForCurrentUser(@Valid @RequestBody OrganizerRequestRequestDto dto) throws EntityNotFoundException, DuplicateEntityException {
        return organizerRequestService.createForCurrentUser(dto);
    }

    @GetMapping("/me")
    public OrganizerRequestResponseDto getCurrentUserRequest() throws EntityNotFoundException {
        return organizerRequestService.getCurrentUserRequest();
    }

    @GetMapping
    public List<OrganizerRequestShortDto> getAllRequests() {
        return organizerRequestService.getAllRequests();
    }

    @GetMapping("/pending")
    public List<OrganizerRequestShortDto> getPendingRequests() {
        return organizerRequestService.getPendingRequests();
    }

    @GetMapping("/{id}")
    public OrganizerRequestResponseDto getRequestById(@PathVariable Long id) throws EntityNotFoundException {
        return organizerRequestService.getRequestById(id);
    }

    @PatchMapping("/{id}/review")
    public OrganizerRequestResponseDto reviewRequest(@PathVariable Long id, @RequestParam Boolean approved, @RequestParam(required = false) String reviewComment, @RequestParam(required = false) Boolean sendEmail) throws EntityNotFoundException {
        return organizerRequestService.reviewRequest(id, approved, reviewComment, sendEmail);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRequest(@PathVariable Long id) throws EntityNotFoundException {
        organizerRequestService.deleteRequest(id);
    }
}
