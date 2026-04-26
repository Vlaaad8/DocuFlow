package com.example.facade;

import com.example.TemplateService;
import com.example.jpa.FilledTemplateRepository;
import com.example.login.User;
import com.example.ocr.MappingPort;
import com.example.template.FilledTemplate;
import com.example.template.Template;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

@Component
@AllArgsConstructor
public class DocumentFiller {
    private final MappingPort mappingPort;
    private final FilledTemplateRepository filledTemplateRepository;

    public FilledTemplate fillAndSave(Template template, User user, Path destination, Map<String, String> values) throws IOException {
        mappingPort.fillTemplate(Path.of(template.getStoragePath()), destination, values);

        FilledTemplate filledTemplate = new FilledTemplate(destination.toString(), user, template);
        return filledTemplateRepository.save(filledTemplate);
    }
}
