package ru.ssau.srestapp.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminStatisticsDto {
    private long totalUsers;
    private long totalOrganizers;
    private long pendingEvents;
    private long pendingOrganizerRequests;
}
