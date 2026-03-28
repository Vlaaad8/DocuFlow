package com.example.web;


import com.example.TemplateService;
import com.example.dto.Approval.ApprovalChainDTO;
import com.example.dto.Approval.ApprovalChainOptionDTO;
import com.example.dto.HtmlRequest;
import com.example.dto.TemplateDTO;
import com.example.dto.UpdateRequest;
import com.example.template.TemplateCategory;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    public List<TemplateDTO> getTemplates() {
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
                            @RequestParam("category") TemplateCategory category,
                            @RequestParam("approvalFlow") int approvalFlowID) throws IOException {
        this.templateService.uploadService(file.getInputStream(), name, description, category, approvalFlowID);
    }

    @GetMapping(value = "template/html", produces = "application/json")
    public HtmlRequest getTemplateHtml(@RequestParam("id") int id) {
        return this.templateService.getTemplateHTML(id);
    }

    @PutMapping(value = "template")
    public void updateTemplate(@RequestBody UpdateRequest updateRequest) {
        System.out.println(updateRequest.html());
        this.templateService.updateTemplate(updateRequest.html(), updateRequest.fileName());
    }

    @GetMapping(value = "template/approvalFlows")
    public List<ApprovalChainOptionDTO> getApprovalFlows() {
        return this.templateService.getApprovalChains();
    }

    @PostMapping(value = "template/validate/html")
    public void validateTemplateHTML(@RequestParam("html") String html) {
        this.templateService.validateHTMLTemplate(html);
    }

    @GetMapping(value = "template/chain/{id}")
    public ApprovalChainDTO getApprovalChainById(@PathVariable("id") int id) {
        return this.templateService.getApprovalChainForTemplate(id);

    }

    @PostMapping(value = "template/pdf")
    public ResponseEntity<byte[]> getPDF(@RequestBody int id) {
        try {
            String path = this.templateService.getTemplatePathByID(id);
            Path filePath = Paths.get(path);
            byte[] pdfBytes = Files.readAllBytes(filePath);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"document.pdf\"")
                    .body(pdfBytes);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
