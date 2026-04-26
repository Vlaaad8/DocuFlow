package com.example;

import com.example.approval.*;
import com.example.dto.Generate.GeneratorTemplateApproverDTO;
import com.example.dto.Generate.GeneratorTemplateDTO;
import com.example.dto.UserFieldValueDTO;
import com.example.dtoMapper.TemplateMapper;
import com.example.facade.GenerationFacade;
import com.example.jpa.*;
import com.example.login.Relation;
import com.example.login.User;
import com.example.ocr.MappingPort;
import com.example.security.SignaturePort;
import com.example.template.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Service
@AllArgsConstructor
public class GeneratorService {

    private final TemplateRepository templateRepository;
    private final UserFieldValueRepository userFieldValueRepository;
    private final UserRepository userRepository;
    private final RelationRepository relationRepository;
    private final Path rootFolder = Paths.get("storage/generated");
    private final TemplateMapper templateMapper;
    private final List<String> documentInformation = List.of("Document Number", "Document Expiration Date", "Issuing Authority", "Document Type", "Document Issue Date", "Document Discriminator", "Issued by");

    private final GenerationFacade documentGenerationFacade;

    @Transactional
    public void generateFile(int templateID, int userID,
                             Map<String, String> dateValues, String sourceOfData) {
        Template template = templateRepository.getReferenceById(templateID);
        User user = userRepository.getReferenceById(userID);
        Map<String, String> values = collectValues(template, userID, dateValues, sourceOfData);

        try {
            Files.createDirectories(rootFolder);
            Path destination = rootFolder.resolve(UUID.randomUUID() + ".pdf");

            documentGenerationFacade.generateAndSubmit(template, user, destination, values);

        } catch (Exception e) {
            throw new RuntimeException("Document generation failed", e);
        }
    }

    private boolean isTemporalField(String name) {
        return name.equals("Specific Date") ||
                name.equals("Today's Date") ||
                name.equals("Date Interval");

    }

    private Map<String, String> collectValues(Template template, int userID,
                                              Map<String, String> dateValues,
                                              String sourceOfData) {
        Map<String, String> values = new HashMap<>();
        SourceOfData targetSource = SourceOfData.valueOf(sourceOfData);

        for (Field field : template.getFields()) {
            if (isTemporalField(field.getFieldName())) {
                String dateVal = dateValues.get(field.getFieldName());
                if (dateVal == null) throw new RuntimeException(
                        "Missing value for date field: " + field.getFieldName());
                values.put(field.getRepresentation(), dateVal);
                continue;
            }

            UserFieldValue userFieldValue;
            if (documentInformation.contains(field.getFieldName())) {
                userFieldValue = userFieldValueRepository
                        .findByUser_IdAndField_IdAndSourceOfData(userID, field.getId(), targetSource)
                        .orElseThrow(() -> new RuntimeException(
                                "Informația '" + field.getFieldName() +
                                        "' nu există în sursa selectată (" + sourceOfData + ")"));
            } else {
                userFieldValue = userFieldValueRepository
                        .findByUser_IdAndField_id(userID, field.getId())
                        .orElseThrow(() -> new RuntimeException(
                                "Value for field " + field.getFieldName() +
                                        " not found for user " + userID));
            }
            values.put(field.getRepresentation(), userFieldValue.getValue());
        }
        return values;
    }

    public List<GeneratorTemplateDTO> giveTemplatesWithLock(int userId) {

        List<Template> templates = this.templateRepository.findAll();
        List<Field> userFilledFields = this.userFieldValueRepository.findByUser_Id(userId);

        List<GeneratorTemplateDTO> templateDTOs = new ArrayList<>();
        for (Template template : templates) {
            List<String> temporalFields = new ArrayList<>();
            List<String> missingFields = new ArrayList<>();
            if (!verifyEligibility(userId, template)) {
                continue;
            }
            boolean canFill = canFill(template, userId, userFilledFields, missingFields, temporalFields);
            GeneratorTemplateDTO generatorTemplateDTO = new GeneratorTemplateDTO(templateMapper.toTemplateDTO(template), canFill, missingFields, temporalFields);
            templateDTOs.add(generatorTemplateDTO);
        }
        return templateDTOs;
    }

    private boolean canFill(Template template, int userID, List<Field> userFilledFields, List<String> missingFields, List<String> temporalFields) {
        List<String> requiredDocFields = new ArrayList<>();

        for (Field field : template.getFields()) {
            if (field.getFieldName().equals("Specific Date") || field.getFieldName().equals("Today's Date") ||
                    field.getFieldName().equals("Date Interval")) {
                temporalFields.add(field.getFieldName());
                continue;
            }


            if (documentInformation.contains(field.getFieldName())) {
                requiredDocFields.add(field.getFieldName());
            } else {

                if (!userFilledFields.contains(field)) {
                    missingFields.add(field.getFieldName());
                }
            }
        }


        if (!missingFields.isEmpty()) {
            return false;
        }


        if (requiredDocFields.isEmpty()) {
            return true;
        }


        boolean foundAtLeastOneValidSource = false;
        for (SourceOfData source : SourceOfData.values()) {
            if (verifyInfoSource(requiredDocFields, userID, source)) {
                foundAtLeastOneValidSource = true;
                break;
            }
        }


        if (!foundAtLeastOneValidSource) {
            missingFields.addAll(requiredDocFields);
            return false;
        }

        return true;
    }

    public List<UserFieldValueDTO> getTemplateValues(int templateId, int userID, String sourceOfData) {
        Template template = this.templateRepository.getReferenceById(templateId);
        List<UserFieldValueDTO> fieldValueDTOs = new ArrayList<>();


        SourceOfData selectedSource = SourceOfData.valueOf(sourceOfData);

        for (Field field : template.getFields()) {
            if (field.getFieldName().equals("Specific Date") ||
                    field.getFieldName().equals("Today's Date") ||
                    field.getFieldName().equals("Date Interval")) {
                continue;
            }

            Optional<UserFieldValue> userFieldValueOpt;


            if (documentInformation.contains(field.getFieldName())) {
                userFieldValueOpt = this.userFieldValueRepository
                        .findByUser_IdAndField_IdAndSourceOfData(userID, field.getId(), selectedSource);
            } else {
                userFieldValueOpt = this.userFieldValueRepository
                        .findByUser_IdAndField_id(userID, field.getId());
            }


            if (userFieldValueOpt.isPresent()) {
                UserFieldValue val = userFieldValueOpt.get();
                fieldValueDTOs.add(new UserFieldValueDTO(
                        val.getId(),
                        val.getField().getFieldName(),
                        val.getValue(),
                        val.getSourceOfData()
                ));
            }
        }

        return fieldValueDTOs;
    }

    public List<GeneratorTemplateApproverDTO> getApprovesForTemplate(int templateID, int userID) {
        Template template = this.templateRepository.getReferenceById(templateID);
        User user = this.userRepository.getReferenceById(userID);
        List<GeneratorTemplateApproverDTO> approverDTOS = new ArrayList<>();
        List<ApprovalStep> approvalSteps = template.getApprovalChain().getSteps();

        approverDTOS.add(new GeneratorTemplateApproverDTO(user.getFirstName() + " " + user.getLastName(), user.getRole().toString()));

        for (int i = 1; i < approvalSteps.size(); i++) {
            Relation relation = this.relationRepository.findBySubordinate_IdAndBoss_Role(user.getId(), approvalSteps.get(i).getApproverRole());
            if (relation != null) {
                approverDTOS.add(new GeneratorTemplateApproverDTO(relation.getBoss().getFirstName() + " " + relation.getBoss().getLastName(), relation.getBoss().getRole().toString()));
                user = relation.getBoss();
            }
            //TODO throw error if relation is null, meaning that the user doesn't have a boss with the required role to approve the document
        }
        return approverDTOS;
    }

    public Map<SourceOfData, Boolean> getDataProfile(int userID) {
        Map<SourceOfData, Boolean> dataProfile = new HashMap<>();
        SourceOfData[] sources = this.userFieldValueRepository.findSourceCounts((long) userID);
        for (SourceOfData source : sources) {
            dataProfile.put(source, true);
        }
        SourceOfData[] allSources = SourceOfData.values();
        for (SourceOfData source : allSources) {
            if (!dataProfile.containsKey(source)) {
                dataProfile.put(source, false);
            }
        }
        return dataProfile;
    }

    private boolean verifyEligibility(int userID, Template template) {
        User user = this.userRepository.getReferenceById(userID);
        List<ApprovalStep> approvalSteps = template.getApprovalChain().getSteps();
        for (int i = 1; i < approvalSteps.size(); i++) {
            Relation relation = this.relationRepository.findBySubordinate_IdAndBoss_Role(user.getId(), approvalSteps.get(i).getApproverRole());
            if (relation != null) {
                user = relation.getBoss();
            } else {
                return false;
            }
        }
        return true;
    }

    private boolean verifyInfoSource(List<String> requiredDocumentInformation, int userID, SourceOfData sourceOfData) {
        // 1. Luăm toate valorile pe care le are user-ul din sursa respectivă (ex: tot ce are din PASSPORT)
        List<UserFieldValue> userFieldValues = this.userFieldValueRepository.findByUser_IdAndSourceOfData(userID, sourceOfData);

        // 2. Extragem doar numele câmpurilor pe care le are
        List<String> availableFields = new ArrayList<>();
        for (UserFieldValue userFieldValue : userFieldValues) {
            availableFields.add(userFieldValue.getField().getFieldName());
        }

        // 3. Verificăm dacă TOATE câmpurile necesare documentului se regăsesc în ce are el completat
        for (String requiredField : requiredDocumentInformation) {
            if (!availableFields.contains(requiredField)) {
                return false; // Îi lipsește măcar un câmp vital din sursa asta -> Sursa e invalidă
            }
        }

        return true; // Are absolut tot ce îi trebuie din sursa asta -> Sursa e validă!
    }

    public List<SourceOfData> getValidSourcesForTemplate(int templateID, int userID) {
        Template template = this.templateRepository.getReferenceById(templateID);
        List<String> requiredDocFields = new ArrayList<>();

        for (Field field : template.getFields()) {
            if (documentInformation.contains(field.getFieldName())) {
                requiredDocFields.add(field.getFieldName());
            }
        }

        List<SourceOfData> validSources = new ArrayList<>();

        if (requiredDocFields.isEmpty()) {
            return Arrays.asList(SourceOfData.values());
        }


        for (SourceOfData source : SourceOfData.values()) {
            if (verifyInfoSource(requiredDocFields, userID, source)) {
                validSources.add(source);
            }
        }

        return validSources;
    }

}
