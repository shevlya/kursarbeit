package ru.ssau.srestapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ssau.srestapp.dto.admin.AdminStatisticsDto;
import ru.ssau.srestapp.service.AdminService;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/statistics")
    public AdminStatisticsDto getStatistics() {
        return adminService.getStatistics();
    }
}
