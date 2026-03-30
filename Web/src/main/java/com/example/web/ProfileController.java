package com.example.web;

import com.example.ProfileService;
import com.example.dto.CertificateDTO;
import com.example.dto.SignatureInfo;
import com.example.dto.UserSavedValueDTO;
import com.example.template.UserFieldValue;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ProfileController {

    private final ProfileService service;

    @GetMapping("profile/certificate")
    public CertificateDTO getCertificate(@RequestParam("userID") int userID){
        return this.service.getCertificateInfo(userID);
    }

    @PostMapping("profile/verify")
    public List<SignatureInfo> verifyDocumentSignature(@RequestBody MultipartFile file){
        try {
            return this.service.verifyDocument(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("profile/savedData")
    public List<UserSavedValueDTO> getSavedData(@RequestParam("userID") int userID){
        return this.service.getSavedData(userID);
    }

    @PutMapping("profile/savedData")
    public void updateSavedData(@RequestParam("fieldID") int fieldID, @RequestParam("value") String value){
         this.service.updateField(fieldID, value);
    }
}
