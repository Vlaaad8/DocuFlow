package com.example;

import com.example.approval.ApprovalChain;
import com.example.converter.Convertors;
import com.example.dto.Approval.ApprovalChainDTO;
import com.example.dto.Approval.ApprovalChainOptionDTO;
import com.example.dto.HtmlRequest;
import com.example.dto.TemplateDTO;
import com.example.dtoMapper.ApprovalChainMapper;
import com.example.dtoMapper.TemplateMapper;
import com.example.exceptions.TemplateValidationException;
import com.example.flyWeight.FieldFlyweightFactory;
import com.example.jpa.ApprovalChainRepository;
import com.example.jpa.FieldRepository;
import com.example.jpa.TemplateRepository;
import com.example.template.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class TemplateService {
    private final TemplateTextPort textPort;
    private final FieldRepository fieldRepository;
    private final TemplateRepository templateRepository;
    private final ApprovalChainRepository approvalChainRepository;
    private final ApprovalChainMapper approvalChainMapper;
    private final TemplateMapper templateMapper;
    private final HTMLCleanerPort htmlCleanerPort;
    private final ConvertPort convertPort;
    private final Path rootFolder = Paths.get("storage");
    private final FieldFlyweightFactory fieldFlyweightFactory;

    public void uploadService(InputStream stream, String name, String description, TemplateCategory templateCategory, int approvalFlowID) {

        try {
            Files.createDirectories(rootFolder);

            String saveName = UUID.randomUUID() + ".docx";

            Path destination = rootFolder.resolve(saveName);

            try (InputStream in = stream) {
                Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
            }

            //TODO validare structurala a DOCX


            String path = destination.toAbsolutePath().toString();

            String extractedText = textPort.extract(new FileInputStream(path));
            Set<Field> fields = extractFields(extractedText);
            ApprovalChain approvalFlow = this.approvalChainRepository.getReferenceById(approvalFlowID);
            Template template = new Template(name, templateCategory, description, path, fields, approvalFlow);

            templateRepository.save(template);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validateTemplate(InputStream inputStream) {
        try {
            byte[] content = inputStream.readAllBytes();
            String extractedText = textPort.extract(new ByteArrayInputStream(content));

            if (extractedText != null) {
                extractedText = extractedText.replace('\u00A0', ' ');
                extractedText = extractedText.replaceAll("\\h", " ");
            }

            if (!hasValidFormat(extractedText)) {
                throw new TemplateValidationException("File has not the valid format");
            }

            Set<Field> fields = extractFields(extractedText);
            if (!hasRequiredFields(fields)) {
                throw new TemplateValidationException("Some required fields are missing from this template");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;

    }

    public void delete(int id) {
        Template template = this.templateRepository.getReferenceById(id);
        Path path = Path.of(template.getStoragePath());
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        templateRepository.deleteById(id);
    }

    private boolean verifyParenthesis(String text) {
        int leftParenthesis = 0;
        int rightParenthesis = 0;
        int index = 0;
        while ((index = text.indexOf("{{", index)) != -1) {
            leftParenthesis += 2;
            index++;
        }
        index = 0;
        while ((index = text.indexOf("}}", index)) != -1) {
            rightParenthesis += 2;
            index++;

        }
        return leftParenthesis == rightParenthesis;
    }

    private Set<Field> extractFields(String textContent) {
        Set<Field> matched = new HashSet<>();

        for (FieldFlyweight flyweight :
                fieldFlyweightFactory.getAllFlyweights()) {


            if (flyweight.existsIn(textContent)) {
                matched.add(fieldRepository
                        .getReferenceById(flyweight.getId()));
            }
        }
        return matched;
    }

    private boolean hasRequiredFields(Set<Field> fields) {
        return fieldFlyweightFactory.getAllFlyweights()
                .stream()
                .filter(FieldFlyweight::isRequired)
                .allMatch(required -> fields.stream()
                        .anyMatch(f -> f.getId() == required.getId()));
    }

    private boolean isRealField(String representation) {
        return fieldFlyweightFactory.getAllFlyweights()
                .stream()
                .anyMatch(f -> f.getRepresentation()
                        .equals(representation));
    }


    private boolean hasValidFormat(String text) {
        if (!verifyParenthesis(text)) {
            throw new TemplateValidationException("The number of parenthesis is not even");
        }
        Pattern validField = Pattern.compile("\\{\\{\\s[a-zA-Z]+(_[a-zA-Z]+)*\\s\\}\\}");
        Pattern allFields = Pattern.compile("\\{\\{.*?\\}\\}");
        Matcher allFieldsMatcher = allFields.matcher(text);

        while (allFieldsMatcher.find()) {
            String fieldName = allFieldsMatcher.group();
            if (!validField.matcher(fieldName).matches()) {
                throw new TemplateValidationException("The field " + fieldName + " is not valid");
            }

            if (!isRealField(fieldName)) {
                throw new TemplateValidationException("The field " + fieldName + " is not real");
            }
        }
        return true;
    }

    public List<TemplateDTO> getTemplates() {
        return templateRepository.findAll().stream().map(templateMapper::toTemplateDTO).toList();
    }

    public List<String> getTemplateCategories() {
        List<String> all = new ArrayList<>();
        Arrays.stream(TemplateCategory.values()).map(Enum::name).forEach(all::add);
        return all;
    }

    public HtmlRequest getTemplateHTML(int id) {
        Template template = this.templateRepository.getReferenceById(id);
        Path path = Path.of(template.getStoragePath());

        try (InputStream stream = Files.newInputStream(path)) {
            byte[] content = stream.readAllBytes();
            return new HtmlRequest(Convertors.convertWordToHTML(content), path.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //TODO updateaza si campurile din baza de date daca e nevoie
    @Transactional
    public void updateTemplate(String htmlContent, String fileName) {
        validateHTMLTemplate(htmlContent);

        Path destination = Path.of(fileName);
        try {
            Convertors.convertHTMLToWord(htmlContent, destination);
        } catch (Exception e) {

            throw new RuntimeException(e);
        }


        try {
            String content = this.textPort.extract(new FileInputStream(destination.toFile()));
            Set<Field> fields = extractFields(content);
            for(Field field : fields) {
                System.out.println(field.getFieldName());
            }
            Template template = this.templateRepository.findTemplateByStoragePath(fileName).orElseThrow(() -> new RuntimeException("Template not found"));
            template.getFields().clear();
            template.getFields().addAll(fields);
            this.templateRepository.save(template);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void validateHTMLTemplate(String htmlContent) {
        String plainText = this.htmlCleanerPort.clean(htmlContent);
        plainText = plainText.replace('\u00A0', ' ');
        plainText = plainText.replaceAll("\\h", " ");

        System.out.println(htmlContent);
        if (!hasValidFormat(plainText)) {
            throw new TemplateValidationException("File has not the valid format");
        }

        Set<Field> fields = extractFields(plainText);
        if (!hasRequiredFields(fields)) {
            throw new TemplateValidationException("Some required fields are missing from this template");
        }
    }

    public List<ApprovalChainOptionDTO> getApprovalChains() {
        return this.approvalChainRepository.findAll().stream().map(approvalChainMapper::toApprovalChainOption).toList();
    }

    public ApprovalChainDTO getApprovalChainForTemplate(int templateID) {
        Template template = this.templateRepository.getReferenceById(templateID);
        ApprovalChain approvalChain = template.getApprovalChain();
        return approvalChainMapper.toApprovalChainDTO(approvalChain);
    }

    public String getTemplatePathByID(int id) {
        Template template = this.templateRepository.getReferenceById(id);
        String path = template.getStoragePath();

        String PDFPath = path.replace(".docx", ".pdf");
        if (Files.exists(Path.of(PDFPath))) {
            return PDFPath;
        }
        try {
            this.convertPort.convertWordToPDF(path);
            return PDFPath;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }



}
