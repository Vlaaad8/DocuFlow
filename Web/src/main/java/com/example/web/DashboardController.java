package com.example.web;


import com.example.DashboardService;
import com.example.dto.DashboardDTO;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200/")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("dashboard")
    public DashboardDTO getDashboardData(@RequestParam("userID") int userID) {
        return this.dashboardService.getDashboardData(userID);
    }
}
