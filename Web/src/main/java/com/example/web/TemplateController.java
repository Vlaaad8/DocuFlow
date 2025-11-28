package com.example.web;


import com.example.TemplateService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class TemplateController {

    private TemplateService templateService;

    @PostMapping("template/validate")
    public void validateTemplate(@RequestParam MultipartFile file) throws IOException {
        templateService.uploadTemplate(file.getInputStream(),file.getOriginalFilename());
    }
}
