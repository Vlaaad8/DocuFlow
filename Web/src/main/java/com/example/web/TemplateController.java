package com.example.web;


import com.example.TemplateService;
import com.example.template.Template;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController()
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class TemplateController {

    private TemplateService templateService;

    @PostMapping("template/validate")
    public Template validateTemplate(@RequestParam MultipartFile file) throws IOException {
        return templateService.uploadTemplate(file.getInputStream(),file.getOriginalFilename());
    }
    @GetMapping("template")
    public List<Template> getTemplates() {
        return this.templateService.getTemplates();
    }
}
