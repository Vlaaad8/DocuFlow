package com.example.web;


import com.example.DashboardService;
import com.example.Notification;
import com.example.dto.DashboardDTO;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200/")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("dashboard")
    public DashboardDTO getDashboardData(@RequestParam("userID") int userID) {
        return this.dashboardService.getDashboardData(userID);
    }
    @PostMapping("dashboard/notification")
    public void markNotificationAsRead(@RequestBody List<Notification> notifications) {
        this.dashboardService.markAsRead(notifications);
    }
}
