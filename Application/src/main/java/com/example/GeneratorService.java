package com.example;

import com.example.approval.*;
import com.example.dto.Generate.GeneratorTemplateApproverDTO;
import com.example.dto.UserFieldValueDTO;
import com.example.dto.Generate.GeneratorTemplateDTO;
import com.example.dtoMapper.TemplateMapper;
import com.example.email.EmailPort;
import com.example.jpa.*;
import com.example.login.Relation;
import com.example.login.User;
import com.example.ocr.MappingPort;
import com.example.security.SignaturePort;
import com.example.template.Field;
import com.example.template.FilledTemplate;
import com.example.template.Template;
import com.example.template.UserFieldValue;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private final EmailPort emailPort;
    private final SignaturePort signaturePort;
    private final TemplateMapper templateMapper;


    @Transactional
    public void generateFile(int templateID, int userID) {

        Template template = this.templateRepository.getReferenceById(templateID);
        Map<String, String> values = new HashMap<>();

        for (Field field : template.getFields()) {
            UserFieldValue userFieldValue = this.userFieldValueRepository.findByUser_IdAndField_id(userID, field.getId()).orElseThrow(() -> new RuntimeException("Value for field " + field.getFieldName() + " not found for user " + userID));
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
            //TODO fix error from here
            this.mappingPort.fillTemplate(Path.of(template.getStoragePath()), destination, values);
            User user = this.userRepository.getReferenceById(userID);
            FilledTemplate filledTemplate = new FilledTemplate(destination.toString(), user, template);
            filledTemplateRepository.save(filledTemplate);


            this.signaturePort.signDocument("D:\\Licenta\\DocuFlow\\storage\\security\\certificates\\user_"+userID+".p12", "parola", destination.toString(), destination.toString());

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
            this.approvalRepository.save(approval);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<GeneratorTemplateDTO> giveTemplatesWithLock(int userId) {

        List<Template> templates = this.templateRepository.findAll();
        List<Field> userFilledFields = this.userFieldValueRepository.findByUser_Id(userId);

        List<GeneratorTemplateDTO> templateDTOs = new ArrayList<>();
        for (Template template : templates) {
            GeneratorTemplateDTO generatorTemplateDTO = new GeneratorTemplateDTO(templateMapper.toTemplateDTO(template), canFill(template, userFilledFields));
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

    public List<UserFieldValueDTO> getTemplateValues(int  templateId, int userID) {
        Template template = this.templateRepository.getReferenceById(templateId);
        List<UserFieldValue> userFilledFields = this.userFieldValueRepository.findForUserAndTemplate(userID, template.getId());
        List<UserFieldValueDTO> fieldValueDTOs = new ArrayList<>();
        for (UserFieldValue userFieldValue : userFilledFields) {
            UserFieldValueDTO fieldValueDTO = new UserFieldValueDTO(userFieldValue.getField().getFieldName(), userFieldValue.getValue());
            fieldValueDTOs.add(fieldValueDTO);
        }
        return fieldValueDTOs;

    }

    public List<GeneratorTemplateApproverDTO> getApprovesForTemplate(int templateID, int userID) {
        Template template = this.templateRepository.getReferenceById(templateID);
        User user = this.userRepository.getReferenceById(userID);
        List<GeneratorTemplateApproverDTO> approverDTOS = new ArrayList<>();
        List<ApprovalStep> approvalSteps = template.getApprovalChain().getSteps();

        approverDTOS.add(new GeneratorTemplateApproverDTO(user.getFirstName()+" "+user.getLastName(), user.getRole().toString()));

        for(int i = 1 ;i < approvalSteps.size(); i++){
            Relation relation = this.relationRepository.findBySubordinate_IdAndBoss_Role(user.getId(), approvalSteps.get(i).getApproverRole());
            if(relation != null){
                approverDTOS.add(new GeneratorTemplateApproverDTO(relation.getBoss().getFirstName()+" "+relation.getBoss().getLastName(), relation.getBoss().getRole().toString()));
                user = relation.getBoss();
            }
            //TODO throw error if relation is null, meaning that the user doesn't have a boss with the required role to approve the document
        }
        return approverDTOS;
    }
}
