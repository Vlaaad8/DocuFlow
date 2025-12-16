package com.example;

import com.example.exceptions.TemplateValidationException;
import com.example.jpa.FieldRepository;
import com.example.jpa.TemplateRepository;
import com.example.template.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class TemplateService {
    private final TemplateTextPort textPort;
    private final FieldRepository fieldRepository;
    private final TemplateRepository templateRepository;

    private final Path rootFolder = Paths.get("storage");


    public void uploadService(InputStream stream, String name, String description, TemplateCategory templateCategory) {
        try {
            Files.createDirectories(rootFolder);

            String saveName = UUID.randomUUID() + ".docx";

            Path destination = rootFolder.resolve(saveName);

           try(InputStream in = stream){
               Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
           }

           //TODO validare structurala a DOCX


            String path = destination.toAbsolutePath().toString();

            String extractedText = textPort.extract(new FileInputStream(path));
            Set<Field> fields = extractFields(extractedText);
            Template template = new Template(name, templateCategory, description, path, fields);

            templateRepository.save(template);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validateTemplate(InputStream inputStream){
        try {
            byte[] content = inputStream.readAllBytes();
            String extractedText = textPort.extract(new ByteArrayInputStream(content));
            if (!hasValidFormat(extractedText)) {
                throw new TemplateValidationException("File has not the valid format");
            }

            Set<Field> fields = extractFields(extractedText);
            if (!hasRequiredFields(fields)) {
                throw new TemplateValidationException("Some required fields are missing from this template");
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
        return true;

    }
    public void delete(int id){
        Template template = this.templateRepository.getReferenceById(id);
            Path path = Path.of(template.getStoragePath());
            try {
                Files.deleteIfExists(path);
            }
            catch (IOException e) {
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

    private boolean hasRequiredFields(Set<Field> fields) {
        List<Field> requiredFields = this.fieldRepository.getFieldByRequired(true);
        for (Field field : requiredFields) {
            if (!fields.contains(field)) {
                return false;
            }
        }
        return true;
    }

    private boolean isRealField(List<Field> fields, String text) {
        for (Field field : fields) {
            if (field.getRepresentation().equals(text)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasValidFormat(String text) {
        if (!verifyParenthesis(text)) {
            throw new TemplateValidationException("The number of parenthesis is not even");
        }
        Pattern validField = Pattern.compile("\\{\\{\\s[a-zA-Z]+(_[a-zA-Z]+)*\\s\\}\\}");
        Pattern allFields = Pattern.compile("\\{\\{.*?\\}\\}");
        List<Field> trueFields = this.fieldRepository.findAll();
        Matcher allFieldsMatcher = allFields.matcher(text);

        while (allFieldsMatcher.find()) {
            String fieldName = allFieldsMatcher.group();
            if (!validField.matcher(fieldName).matches()) {
                throw new TemplateValidationException("The field " + fieldName + " is not valid");
            }

            if (!isRealField(trueFields, fieldName)) {
                throw new TemplateValidationException("The field " + fieldName + " is not real");
            }
        }
        return true;
    }

    private Set<Field> extractFields(String text) {
        Set<Field> fields = new HashSet<>();
        for (Field field : fieldRepository.findAll()) {
            if (text.contains(field.getRepresentation())) {
                fields.add(field);
            }
        }
        return fields;
    }

    public List<Template> getTemplates() {
        return templateRepository.findAll();
    }

    public List<String> getTemplateCategories(){
        List<String> all = new ArrayList<>();
        Arrays.stream(TemplateCategory.values()).map(Enum::name).forEach(all::add);
        return all;
    }


}
