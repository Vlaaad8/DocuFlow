package com.example.web;


import com.example.TemplateService;
import com.example.template.Template;
import com.example.web.requests.TemplateRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController()
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class TemplateController {

    private TemplateService templateService;

    @PostMapping("template/validate")
    public boolean validateTemplate(@RequestParam("file") MultipartFile file) throws IOException {
        return templateService.validateTemplate(file.getInputStream());
    }

    @GetMapping("template")
    public List<Template> getTemplates() {
        return this.templateService.getTemplates();
    }

    @DeleteMapping("template")
    public void deleteTemplate(@RequestParam() int id) {
        this.templateService.delete(id);
    }

    @GetMapping("template/category")
    public List<String> getTemplateCategories() {
        return this.templateService.getTemplateCategories();
    }

    @PostMapping("template")
    public void addTemplate(@RequestBody TemplateRequest templateRequest) throws IOException {
        this.templateService.uploadService(templateRequest.file(),templateRequest.name(),templateRequest.description(),templateRequest.category());
    }

}
