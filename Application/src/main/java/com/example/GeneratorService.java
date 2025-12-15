package com.example;

import com.example.jpa.FilledTemplateRepository;
import com.example.jpa.TemplateRepository;
import com.example.jpa.UserFieldValueRepository;
import com.example.jpa.UserRepository;
import com.example.login.User;
import com.example.ocr.MappingPort;
import com.example.template.*;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

//TODO generation flow : check if a user has enough data to generate a document , and then give him the document
@Service
public class GeneratorService {

    private final TemplateRepository templateRepository;
    private final UserFieldValueRepository userFieldValueRepository;
    private final UserRepository userRepository;
    private final FilledTemplateRepository filledTemplateRepository;

    private final Path rootFolder = Paths.get("storage/generated");


    public GeneratorService(TemplateRepository templateRepository, UserFieldValueRepository userFieldValueRepository,UserRepository userRepository,FilledTemplateRepository filledTemplateRepository,MappingPort mappingPort) {
        this.templateRepository = templateRepository;
        this.userFieldValueRepository = userFieldValueRepository;
        this.userRepository = userRepository;
        this.filledTemplateRepository = filledTemplateRepository;
        this.mappingPort = mappingPort;
    }

    private final MappingPort mappingPort;

    public void generateFile(int templateID, int userID) {

        Template template = this.templateRepository.getReferenceById(templateID);
        Map<String, String> values = new HashMap<>();

        for (Field field : template.getFields()) {
            UserFieldValue userFieldValue = this.userFieldValueRepository.findByUser_IdAndField_id(userID, field.getId()).orElse(null);
            if (userFieldValue != null) {
                values.put(userFieldValue.getField().getRepresentation(), userFieldValue.getValue());
            }
        }
        try {
            Files.createDirectories(rootFolder);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        String generateName= UUID.randomUUID()+".docx";
        Path destination = rootFolder.resolve(generateName);

        try {
            this.mappingPort.fillTemplate(Path.of(template.getStoragePath()), destination, values);
            User user = this.userRepository.getReferenceById(userID);
            FilledTemplate filledTemplate = new FilledTemplate(destination.toString(),user,template);
            filledTemplateRepository.save(filledTemplate);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
