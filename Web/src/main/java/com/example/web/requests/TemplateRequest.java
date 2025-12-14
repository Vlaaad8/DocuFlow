package com.example.web.requests;

import com.example.template.TemplateCategory;
import org.springframework.web.multipart.MultipartFile;

public record TemplateRequest(MultipartFile file, String name, String description, TemplateCategory category) {
}
