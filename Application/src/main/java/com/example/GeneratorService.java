package com.example;

import com.example.jpa.TemplateRepository;
import com.example.jpa.UserFieldValueRepository;
import com.example.template.Field;
import com.example.template.Template;
import com.example.template.TemplateTextPort;
import com.example.template.UserFieldValue;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

//TODO generation flow : check if a user has enough data to generate a document , and then give him the document
@Service
public class GeneratorService {

    private final TemplateRepository templateRepository;
    private final UserFieldValueRepository userFieldValueRepository;

    public GeneratorService(TemplateRepository templateRepository, UserFieldValueRepository userFieldValueRepository) {
        this.templateRepository = templateRepository;
        this.userFieldValueRepository = userFieldValueRepository;
    }

    private TemplateTextPort templatePort;

    public File generateFile(int templateID, int userID) {
        Template template = this.templateRepository.getReferenceById(templateID);

        File realTemplate = new File(template.getStoragePath());
        try (InputStream stream = new FileInputStream(realTemplate)) {
            String extractedText = templatePort.extract(stream);
            //TODO it maybe replaces just one field per value
            for (Field field : template.getFields()) {
                UserFieldValue value = userFieldValueRepository.findByUser_IdAndField_id(userID, field.getId()).orElse(null);
                String realValue = value.getValue();
                extractedText = extractedText.replace(field.getRepresentation(), realValue);
            }
            try {

                Path outPath = Paths.get("generated", "template-" + templateID + "-user-" + userID + ".txt");
                Files.createDirectories(outPath.getParent());

                Files.writeString(outPath, extractedText, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                return outPath.toFile();
            } catch (IOException e) {
                throw new RuntimeException("Failed to write output txt", e);
        }

        }catch (Exception e) {
            throw new RuntimeException("Failed to generate file", e);
        }
        }
    }
