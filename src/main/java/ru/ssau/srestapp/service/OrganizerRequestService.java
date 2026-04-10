package ru.ssau.srestapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.srestapp.dto.organizerRequest.*;
import ru.ssau.srestapp.entity.*;
import ru.ssau.srestapp.exception.*;
import ru.ssau.srestapp.repository.OrganizerRequestRepository;
import ru.ssau.srestapp.repository.RoleRepository;
import ru.ssau.srestapp.repository.UserRepository;
import ru.ssau.srestapp.security.CustomUserDetails;
import ru.ssau.srestapp.util.SecurityUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizerRequestService {

    private static final String ROLE_ORGANIZER = "ORGANIZER";

    private final OrganizerRequestRepository organizerRequestRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;

    @Transactional
    public OrganizerRequestResponseDto createForCurrentUser(OrganizerRequestRequestDto dto) throws EntityNotFoundException, DuplicateEntityException {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (organizerRequestRepository.findByUserIdAndActiveStatus(currentUserId, List.of(RequestStatus.PENDING)).isPresent()) {
            throw new DuplicateEntityException("У вас уже есть заявка на рассмотрении");
        }
        User user = findUserOrThrow(currentUserId);
        if (ROLE_ORGANIZER.equalsIgnoreCase(user.getRole().getRoleName())) {
            throw new DuplicateEntityException("Вы уже являетесь организатором");
        }
        OrganizerRequest entity = new OrganizerRequest();
        entity.setUser(user);
        entity.setRequestStatus(RequestStatus.PENDING);
        entity.setRequestText(dto.getRequestText());
        entity.setSubmittedAt(LocalDateTime.now());
        return toDto(organizerRequestRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public OrganizerRequestResponseDto getCurrentUserRequest() throws EntityNotFoundException {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        OrganizerRequest request = organizerRequestRepository.findLatestByUserId(currentUserId).orElseThrow(() -> new EntityNotFoundException("Заявка не найдена"));
        return toDto(request);
    }

    @Transactional(readOnly = true)
    public List<OrganizerRequestShortDto> getAllRequests() {
        return organizerRequestRepository.findAllWithUser().stream().map(this::toShortDto).toList();
    }

    @Transactional(readOnly = true)
    public List<OrganizerRequestShortDto> getPendingRequests() {
        return organizerRequestRepository.findByRequestStatus(RequestStatus.PENDING)
                .stream().map(this::toShortDto).toList();
    }

    @Transactional(readOnly = true)
    public OrganizerRequestResponseDto getRequestById(Long id) throws EntityNotFoundException {
        return toDto(findOrThrow(id));
    }

    @Transactional
    public OrganizerRequestResponseDto reviewRequest(Long id, Boolean approved, String reviewComment, Boolean sendEmail) throws EntityNotFoundException {
        OrganizerRequest request = findOrThrow(id);
        CustomUserDetails adminDetails = SecurityUtils.getCurrentUserDetails();
        User admin = findUserOrThrow(adminDetails.getUserId());
        request.setAdmin(admin);
        request.setReviewComment(reviewComment);
        request.setReviewedAt(LocalDateTime.now());
        if (approved) {
            request.setRequestStatus(RequestStatus.APPROVED);
            User user = request.getUser();
            Role organizerRole = findOrganizerRoleOrThrow();
            user.setRole(organizerRole);
            userRepository.save(user);
            if (sendEmail != null && sendEmail) {
                emailService.sendOrganizerRequestApproved(user.getEmail(), user.getFio());
            }
        } else {
            request.setRequestStatus(RequestStatus.REJECTED);
            if (sendEmail != null && sendEmail) {
                emailService.sendOrganizerRequestRejected(
                        request.getUser().getEmail(),
                        request.getUser().getFio(),
                        reviewComment
                );
            }
        }
        return toDto(organizerRequestRepository.save(request));
    }

    @Transactional
    public void deleteRequest(Long id) throws EntityNotFoundException {
        findOrThrow(id);
        organizerRequestRepository.deleteById(id);
    }

    private Role findOrganizerRoleOrThrow() throws EntityNotFoundException {
        return roleRepository.findByRoleName(ROLE_ORGANIZER).orElseThrow(() -> new EntityNotFoundException(EntityType.ROLE.notFound(ROLE_ORGANIZER)));
    }

    private OrganizerRequest findOrThrow(Long id) throws EntityNotFoundException {
        return organizerRequestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(EntityType.ORGANIZER_REQUEST.notFoundFeminine(id)));
    }

    private User findUserOrThrow(Long id) throws EntityNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(EntityType.USER.notFound(id)));
    }

    private OrganizerRequestResponseDto toDto(OrganizerRequest r) {
        return new OrganizerRequestResponseDto(
                r.getIdOrganizerRequest(),
                r.getUser().getIdUser(),
                r.getUser().getFio(),
                r.getRequestStatus(),
                r.getRequestText(),
                r.getReviewComment(),
                r.getSubmittedAt(),
                r.getReviewedAt()
        );
    }

    private OrganizerRequestShortDto toShortDto(OrganizerRequest r) {
        return new OrganizerRequestShortDto(
                r.getIdOrganizerRequest(),
                r.getUser().getIdUser(),
                r.getUser().getFio(),
                r.getUser().getEmail(),
                r.getRequestStatus(),
                r.getRequestText(),
                r.getSubmittedAt()
        );
    }
}
