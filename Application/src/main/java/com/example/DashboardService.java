package com.example;

import com.example.dto.DashboardDTO;
import com.example.jpa.FilledTemplateRepository;
import com.example.jpa.TemplateRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DashboardService {

    private final TemplateRepository templateRepository;
    private final FilledTemplateRepository filledTemplateRepository;


    public DashboardDTO getDashboardData(int userID) {
        int totalTemplates = (int) templateRepository.count();
        int totalFilledTemplates = filledTemplateRepository.countByUserId(userID);

        return new DashboardDTO(totalTemplates, totalFilledTemplates, -1, -1);
    }

}
