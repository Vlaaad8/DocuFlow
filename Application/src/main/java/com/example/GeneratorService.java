package com.example;

import com.example.approval.*;
import com.example.dto.Generate.GeneratorTemplateApproverDTO;
import com.example.dto.Generate.GeneratorTemplateDTO;
import com.example.dto.UserFieldValueDTO;
import com.example.dtoMapper.TemplateMapper;
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
    private final FilledTemplateRepository filledTemplateRepository;
    private final RelationRepository relationRepository;
    private final ApprovalRequestRepository approvalRequestRepository;
    private final ApprovalRepository approvalRepository;

    private final Path rootFolder = Paths.get("storage/generated");
    private final MappingPort mappingPort;
    private final SignaturePort signaturePort;
    private final TemplateMapper templateMapper;

    private final List<String> staticInformation = List.of("First Name", "Date of Birth", "Place of Birth", "Personal Number", "Sex");
    private final List<String> volatileInformation = List.of("Address", "Nationality", "Last Name");
    private final List<String> documentInformation = List.of("Document Number", "Document Expiration Date", "Issuing Authority", "Document Type", "Document Issue Date", "Document Discriminator", "Issued by");


    @Transactional
    public void generateFile(int templateID, int userID, Map<String, String> dateValues, String sourceOfData) {

        Template template = this.templateRepository.getReferenceById(templateID);
        Map<String, String> values = new HashMap<>();

        for (Field field : template.getFields()) {
            if (field.getFieldName().equals("Specific Date") || field.getFieldName().equals("Today's Date") ||
                    field.getFieldName().equals("Date Interval")) {
                if (dateValues.containsKey(field.getFieldName())) {
                    values.put(field.getRepresentation(), dateValues.get(field.getFieldName()));
                } else {
                    throw new RuntimeException("Missing value for date field: " + field.getFieldName());
                }
                continue;
            }

            UserFieldValue userFieldValue = null;

            if (documentInformation.contains(field.getFieldName())) {

                SourceOfData targetSource = SourceOfData.valueOf(sourceOfData);

                userFieldValue = this.userFieldValueRepository
                        .findByUser_IdAndField_IdAndSourceOfData(userID, field.getId(), targetSource)
                        .orElseThrow(() -> new RuntimeException("Informația '" + field.getFieldName() + "' nu există în sursa selectată (" + sourceOfData + ")"));

            } else {

                userFieldValue = this.userFieldValueRepository
                        .findByUser_IdAndField_id(userID, field.getId())
                        .orElseThrow(() -> new RuntimeException("Value for field " + field.getFieldName() + " not found for user " + userID));
            }


            if (userFieldValue != null) {
                values.put(field.getRepresentation(), userFieldValue.getValue());
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
            //TODO fix error from here
            this.mappingPort.fillTemplate(Path.of(template.getStoragePath()), destination, values);
            User user = this.userRepository.getReferenceById(userID);
            FilledTemplate filledTemplate = new FilledTemplate(destination.toString(), user, template);
            filledTemplateRepository.save(filledTemplate);

            this.signaturePort.prepareForSigning(destination.toString(), template.getApprovalChain().getSteps().size());
            this.signaturePort.signDocument("D:\\Licenta\\DocuFlow\\storage\\security\\certificates\\user_" + userID + ".p12", "parola", destination.toString(), 0);

            ApprovalRequest approvalRequest = new ApprovalRequest();
            approvalRequest.setTemplate(filledTemplate);
            approvalRequest.setApprovalChain(template.getApprovalChain());
            approvalRequest.setStatus(ApprovalRequestStatus.PENDING);
            approvalRequest.setCurrentStep(1);

            ApprovalRequest savedApprovalRequest = this.approvalRequestRepository.saveAndFlush(approvalRequest);

            Approval approval = new Approval();
            approval.setStatus(ApprovalStatus.IN_PROGRESS);
            approval.setApprovalRequest(approvalRequest);
            User approver = this.relationRepository.findBossBySubordinate_IdAndBoss_Role(userID, filledTemplate.getTemplate().getApprovalChain().getSteps().get(1).getApproverRole());
            approval.setApprover(approver);
            approval.setStepNumber(1);
            approval.setDecisionDate(Timestamp.from(Instant.now()));
            this.approvalRepository.save(approval);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<GeneratorTemplateDTO> giveTemplatesWithLock(int userId) {

        List<Template> templates = this.templateRepository.findAll();
        List<Field> userFilledFields = this.userFieldValueRepository.findByUser_Id(userId);
        List<String> temporalFields = new ArrayList<>();

        List<GeneratorTemplateDTO> templateDTOs = new ArrayList<>();
        for (Template template : templates) {
            List<String> missingFields = new ArrayList<>();
            if (!verifyEligibility(userId, template)) {
                continue;
            }
            boolean canFill = canFill(template, userId,userFilledFields, missingFields, temporalFields);
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

            // Dacă e informație de document (Serie, Nr, etc.), o punem deoparte ca să o verificăm pe surse
            if (documentInformation.contains(field.getFieldName())) {
                requiredDocFields.add(field.getFieldName());
            } else {
                // Dacă e informație normală (Statică/Volatilă), verificăm direct dacă user-ul o are
                if (!userFilledFields.contains(field)) {
                    missingFields.add(field.getFieldName());
                }
            }
        }

        // Dacă îi lipsesc informații de bază (ex: Nume, Adresă), clar nu poate genera
        if (!missingFields.isEmpty()) {
            return false;
        }

        // Dacă documentul nu cere nicio informație specifică actelor de identitate, e gata de generat
        if (requiredDocFields.isEmpty()) {
            return true;
        }

        // MAGIA AICI: Căutăm dacă MĂCAR O SURSĂ are toate `requiredDocFields`
        boolean foundAtLeastOneValidSource = false;
        for (SourceOfData source : SourceOfData.values()) {
            if (verifyInfoSource(requiredDocFields, userID, source)) {
                foundAtLeastOneValidSource = true;
                break; // Am găsit o sursă completă! Oprim căutarea.
            }
        }

        // Dacă nu a găsit nicio sursă completă, adăugăm câmpurile de document la lista de lipsuri
        if (!foundAtLeastOneValidSource) {
            missingFields.addAll(requiredDocFields);
            return false;
        }

        return true;
    }

    public List<UserFieldValueDTO> getTemplateValues(int templateId, int userID, String sourceOfData) {
        Template template = this.templateRepository.getReferenceById(templateId);
        List<UserFieldValueDTO> fieldValueDTOs = new ArrayList<>();

        // Convertim string-ul venit din Angular în Enum
        SourceOfData selectedSource = SourceOfData.valueOf(sourceOfData);

        for (Field field : template.getFields()) {
            // 1. Sărim peste câmpurile de timp, pentru că ele sunt completate din input-urile de pe frontend
            if (field.getFieldName().equals("Specific Date") ||
                    field.getFieldName().equals("Today's Date") ||
                    field.getFieldName().equals("Date Interval")) {
                continue;
            }

            Optional<UserFieldValue> userFieldValueOpt;

            // 2. Dacă este un câmp de document (ex: Document Number), forțăm sursa selectată
            if (documentInformation.contains(field.getFieldName())) {
                userFieldValueOpt = this.userFieldValueRepository
                        .findByUser_IdAndField_IdAndSourceOfData(userID, field.getId(), selectedSource);
            } else {
                // 3. Dacă este o informație generală (Nume, Adresă), o luăm standard
                userFieldValueOpt = this.userFieldValueRepository
                        .findByUser_IdAndField_id(userID, field.getId());
            }

            // 4. Dacă am găsit valoarea, o adăugăm în lista pentru frontend
            if (userFieldValueOpt.isPresent()) {
                UserFieldValue val = userFieldValueOpt.get();
                fieldValueDTOs.add(new UserFieldValueDTO(
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
