package com.example.web;


import com.example.TemplateService;
import com.example.template.Template;
import com.example.template.TemplateCategory;
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
    public void addTemplate(@RequestParam("file") MultipartFile file,
                            @RequestParam("name") String name,
                            @RequestParam("description") String description,
                            @RequestParam("category") TemplateCategory category) throws IOException {
        this.templateService.uploadService(file.getInputStream(),name, description, category);
    }

}
