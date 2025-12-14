package com.example;

import com.example.exceptions.TemplateValidationException;
import com.example.jpa.FieldRepository;
import com.example.jpa.TemplateRepository;
import com.example.template.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class TemplateService {
    //TODO when delete: remove the file also from storage, not just DB
    private final TemplateTextPort textPort;
    private final FieldRepository fieldRepository;
    private final TemplateRepository templateRepository;
    private final Path rootFolder = Paths.get("storage");

    //TODO refactor all this, this just uploads , doesnt validate anymore
//    public Template uploadTemplate(InputStream inputStream, String fileName) {
//        try {
//            byte[] content = inputStream.readAllBytes();
//            String extractedText = textPort.extract(new ByteArrayInputStream(content));
//
//            //am scos validarile
//
//            Files.createDirectories(rootFolder);
//            String safeFileName = (fileName == null || fileName.isBlank())
//                    ? "template_" + UUID.randomUUID()
//                    : fileName;
//            String storedFileName = UUID.randomUUID() + "_" + safeFileName;
//
//            Path destination = rootFolder.resolve(storedFileName);
//            Files.write(destination, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
//
//            String storagePath = destination.toString();
////            Template template = new Template(safeFileName, TemplateCategory.NOTICE, TemplateStatus.VALID, storagePath, fields);
////
////            return templateRepository.save(template);
//
//        } catch (RuntimeException | IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public void uploadService(MultipartFile file, String name, String description, TemplateCategory templateCategory) {
        try {
            Files.createDirectories(rootFolder);

            String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));

            String saveName = UUID.randomUUID() + extension;

            Path destination = rootFolder.resolve(saveName);

            file.transferTo(destination);

            File storedFile = destination.toFile();

            String path = storedFile.getAbsolutePath();

            String extractedText = textPort.extract(new FileInputStream(storedFile));
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
            return false;
        }
        Pattern validField = Pattern.compile("\\{\\{ [a-zA-Z]+ \\}\\}");
        Pattern allFields = Pattern.compile("\\{\\{.*?\\}\\}");
        List<Field> trueFields = this.fieldRepository.findAll();
        Matcher allFieldsMatcher = allFields.matcher(text);

        while (allFieldsMatcher.find()) {
            String fieldName = allFieldsMatcher.group();
            if (!validField.matcher(fieldName).matches()) {
                return false;
            }

            if (!isRealField(trueFields, fieldName)) {
                return false;
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
