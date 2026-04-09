package ru.ssau.srestapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ssau.srestapp.dto.admin.AdminStatisticsDto;
import ru.ssau.srestapp.entity.RequestStatus;
import ru.ssau.srestapp.repository.EventRepository;
import ru.ssau.srestapp.repository.OrganizerRequestRepository;
import ru.ssau.srestapp.repository.RoleRepository;
import ru.ssau.srestapp.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EventRepository eventRepository;
    private final OrganizerRequestRepository organizerRequestRepository;

    public AdminStatisticsDto getStatistics() {
        long totalUsers = userRepository.count();
        long totalOrganizers = roleRepository.findByRoleName("ORGANIZER")
                .map(role -> userRepository.countByRole(role))
                .orElse(0L);
        long pendingEvents = eventRepository.countByVerifiedFalse();
        long pendingOrganizerRequests = organizerRequestRepository.countByRequestStatus(RequestStatus.PENDING);
        return new AdminStatisticsDto(totalUsers, totalOrganizers, pendingEvents, pendingOrganizerRequests);
    }
}
