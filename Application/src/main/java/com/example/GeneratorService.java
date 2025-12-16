package com.example;

import com.example.dto.FieldValueDTO;
import com.example.dto.GeneratorTemplateDTO;
import com.example.jpa.FilledTemplateRepository;
import com.example.jpa.TemplateRepository;
import com.example.jpa.UserFieldValueRepository;
import com.example.jpa.UserRepository;
import com.example.login.User;
import com.example.ocr.MappingPort;
import com.example.template.Field;
import com.example.template.FilledTemplate;
import com.example.template.Template;
import com.example.template.UserFieldValue;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class GeneratorService {

    private final TemplateRepository templateRepository;
    private final UserFieldValueRepository userFieldValueRepository;
    private final UserRepository userRepository;
    private final FilledTemplateRepository filledTemplateRepository;

    private final Path rootFolder = Paths.get("storage/generated");
    private final MappingPort mappingPort;

    public GeneratorService(TemplateRepository templateRepository, UserFieldValueRepository userFieldValueRepository, UserRepository userRepository, FilledTemplateRepository filledTemplateRepository, MappingPort mappingPort) {
        this.templateRepository = templateRepository;
        this.userFieldValueRepository = userFieldValueRepository;
        this.userRepository = userRepository;
        this.filledTemplateRepository = filledTemplateRepository;
        this.mappingPort = mappingPort;
    }

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
        } catch (IOException e) {
            e.printStackTrace();
        }
        String generateName = UUID.randomUUID() + ".pdf";
        Path destination = rootFolder.resolve(generateName);

        try {
            this.mappingPort.fillTemplate(Path.of(template.getStoragePath()), destination, values);
            User user = this.userRepository.getReferenceById(userID);
            FilledTemplate filledTemplate = new FilledTemplate(destination.toString(), user, template);
            filledTemplateRepository.save(filledTemplate);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<GeneratorTemplateDTO> giveTemplatesWithLock(int userId) {
        List<Template> templates = this.templateRepository.findAll();
        List<Field> userFilledFields = this.userFieldValueRepository.findByUser_Id(userId);
        List<GeneratorTemplateDTO> templateDTOs = new ArrayList<>();
        for (Template template : templates) {
            GeneratorTemplateDTO generatorTemplateDTO = new GeneratorTemplateDTO(template, canFill(template, userFilledFields));
            templateDTOs.add(generatorTemplateDTO);
        }
        return templateDTOs;
    }

    private boolean canFill(Template template, List<Field> userFilledFields) {
        for (Field field : template.getFields()) {
            if (!userFilledFields.contains(field)) {
                return false;
            }
        }
        return true;
    }

    public List<FieldValueDTO> getTemplateValues(int  templateId, int userID) {
        Template template = this.templateRepository.getReferenceById(templateId);
        List<UserFieldValue> userFilledFields = this.userFieldValueRepository.findForUserAndTemplate(userID, template.getId());
        List<FieldValueDTO> fieldValueDTOs = new ArrayList<>();
        for (UserFieldValue userFieldValue : userFilledFields) {
            FieldValueDTO fieldValueDTO = new FieldValueDTO(userFieldValue.getField().getFieldName(), userFieldValue.getValue());
            fieldValueDTOs.add(fieldValueDTO);
        }
        return fieldValueDTOs;

    }
}
