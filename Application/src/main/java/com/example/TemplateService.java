package com.example;

import com.example.jpa.FieldRepository;
import com.example.jpa.TemplateRepository;
import com.example.template.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TemplateService {

    private final TemplateTextPort textPort;
    private final FieldRepository fieldRepository;
    private final TemplateRepository templateRepository;
    private final Path rootFolder = Paths.get("storage");

    public TemplateService(TemplateTextPort textPort, FieldRepository fieldRepository, TemplateRepository templateRepository) {
        this.textPort = textPort;
        this.fieldRepository = fieldRepository;
        this.templateRepository = templateRepository;
    }

    //TODO file upload to disk and refactor code
    public Template uploadTemplate(InputStream inputStream, String fileName) {
        try {
            byte[] content = inputStream.readAllBytes();
            String extractedText = textPort.extract(new ByteArrayInputStream(content), fileName);


            if (!hasValidFormat(extractedText)) {
                throw new RuntimeException("File has not the valid format");
            }

            Set<Field> fields = extractFields(extractedText);

            Files.createDirectories(rootFolder);
            String safeFileName = (fileName == null || fileName.isBlank())
                    ? "template_" + UUID.randomUUID()
                    : fileName;
            String storedFileName = UUID.randomUUID() + "_" + safeFileName;

            Path destination = rootFolder.resolve(storedFileName);
            Files.write(destination, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            String storagePath = destination.toString();
            Template template = new Template(safeFileName, TemplateCategory.NOTICE, TemplateStatus.VALID, storagePath, fields);

            return templateRepository.save(template);

        } catch (RuntimeException | IOException e) {
            throw new RuntimeException(e);
        }
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

    private boolean hasRequiredFields(List<Field> fields, String text) {
        for (Field field : fields) {
            if (!text.contains(field.getRepresentation())) {
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
        //TODO : verifica ca toate campurile required sunt in template
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

    public List<Template> getTemplates(){
        return templateRepository.findAll();
    }


}
