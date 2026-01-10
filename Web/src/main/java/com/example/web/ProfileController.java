package com.example.web;

import com.example.ProfileService;
import com.example.dto.CertificateDTO;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ProfileController {

    private final ProfileService service;

    @GetMapping("profile/certificate")
    public CertificateDTO getCertificate(@RequestParam("userID") int userID){
        return this.service.getCertificateInfo(userID);
    }
}
