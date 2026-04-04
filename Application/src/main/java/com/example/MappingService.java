package com.example;

import com.example.exceptions.FieldException;
import com.example.exceptions.UserException;
import com.example.jpa.FieldMapperRepository;
import com.example.jpa.UserFieldValueRepository;
import com.example.jpa.UserRepository;
import com.example.login.User;
import com.example.ocr.ExtractedField;
import com.example.template.Field;
import com.example.template.SourceOfData;
import com.example.template.UserFieldValue;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@AllArgsConstructor
@Service
public class MappingService {

    private final FieldMapperRepository fieldMapperRepository;
    private final UserRepository userRepository;
    private final UserFieldValueRepository userFieldValueRepository;

    private final List<String> staticInformation = List.of("First Name", "Date of Birth", "Place of Birth", "Personal Number","Sex", "Vehicle Classifications");
    private final List<String> volatileInformation = List.of("Address", "Nationality", "Last Name");
    private final List<String> documentInformation = List.of("Document Number", "Document Expiration Date", "Issuing Authority", "Document Type", "Document Issue Date", "Document Discriminator","Issued by");


    public UserFieldValue mapField(ExtractedField azureField, int userId) {

        Field field = fieldMapperRepository.getByAzureFieldName(azureField.getLabel())
                .orElseThrow(() -> new FieldException("No mapping found for Azure field: " + azureField.getLabel()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(userId));
        return new UserFieldValue(
                azureField.getValue(),
                field,
                user,
                mapSourceOfData(azureField.getSourceOfData()),
                azureField.getConfidence()
        );
    }

    public void saveFields(List<ExtractedField> extractedFields, int userId) {


        // Step 1: Map Azure fields to UserFieldValue entities
        List<UserFieldValue> toSaveValues = extractedFields.stream()
                .map(field -> mapField(field, userId))
                .toList();

        // Step 2: Get all existing UserFieldValue entries for the user
        List<UserFieldValue> existingValues = userFieldValueRepository.findAllByUser_Id(userId);

        // Step 3: Split existing values into categories
        List<UserFieldValue> staticValues = existingValues.stream()
                .filter(value -> staticInformation.contains(value.getField().getFieldName()))
                .toList();
        List<UserFieldValue> volatileValues = existingValues.stream()
                .filter(value -> volatileInformation.contains(value.getField().getFieldName()))
                .toList();
        List<UserFieldValue> documentValues = existingValues.stream()
                .filter(value -> documentInformation.contains(value.getField().getFieldName()))
                .toList();

        // Split NEW values into categories
        List<UserFieldValue> newStaticValues = toSaveValues.stream()
                .filter(value -> staticInformation.contains(value.getField().getFieldName()))
                .toList();
        List<UserFieldValue> newVolatileValues = toSaveValues.stream()
                .filter(value -> volatileInformation.contains(value.getField().getFieldName()))
                .toList();
        List<UserFieldValue> newDocumentValues = toSaveValues.stream()
                .filter(value -> documentInformation.contains(value.getField().getFieldName()))
                .toList();

        // Step 4: Update fields based on business rules
        updateStaticInformation(staticValues, newStaticValues);


        String oldIssueDate = extractFieldValue(documentValues);
        String newIssueDate = extractFieldValue(newDocumentValues);
        updateVolatileInformation(volatileValues, newVolatileValues, oldIssueDate, newIssueDate);

        updateDocumentInformation(documentValues, newDocumentValues);
    }

    private SourceOfData mapSourceOfData(String source) {
        return switch (source) {
            case "idDocument.nationalIdentityCard" -> SourceOfData.NATIONAL_IDENTITY_CARD;
            case "idDocument.passport" -> SourceOfData.PASSPORT;
            case "idDocument.driverLicense" -> SourceOfData.DRIVER_LICENSE;
            case "idDocument.residencePermit" -> SourceOfData.RESIDENCE_PERMIT;
            case "idDocument.usSocialSecurityCard" -> SourceOfData.SOCIAL_SECURITY_CARD;
            case "manualEntry" -> SourceOfData.MANUAL_ENTRY;
            default -> SourceOfData.UNKNOWN;
        };
    }

    private void updateStaticInformation(List<UserFieldValue> existingStaticValues, List<UserFieldValue> newStaticValues) {
        for (UserFieldValue newValue : newStaticValues) {
            UserFieldValue existingValue = existingStaticValues.stream()
                    .filter(value -> value.getField().getId() == newValue.getField().getId())
                    .findFirst()
                    .orElse(null);

            if (existingValue == null) {
                userFieldValueRepository.save(newValue);
            } else if (newValue.getConfidence() > existingValue.getConfidence()) {
                existingValue.setValue(newValue.getValue());
                existingValue.setConfidence(newValue.getConfidence());

                userFieldValueRepository.save(existingValue);
            }
        }
    }

    private void updateVolatileInformation(List<UserFieldValue> existingVolatileValues, List<UserFieldValue> newVolatileValues, String oldIssueDateStr, String newIssueDateStr) {
        boolean isNewerDocument = isDateNewer(newIssueDateStr, oldIssueDateStr);

        for (UserFieldValue newValue : newVolatileValues) {
            UserFieldValue existingValue = existingVolatileValues.stream()
                    .filter(value -> value.getField().getId() == newValue.getField().getId())
                    .findFirst()
                    .orElse(null);

            if (existingValue == null) {
                userFieldValueRepository.save(newValue);
            } else if (isNewerDocument || (newValue.getConfidence() > existingValue.getConfidence() && !isDateNewer(oldIssueDateStr, newIssueDateStr))) {

                existingValue.setValue(newValue.getValue());
                existingValue.setConfidence(newValue.getConfidence());
                existingValue.setSourceOfData(newValue.getSourceOfData());
                userFieldValueRepository.save(existingValue);
            }
        }
    }

    private void updateDocumentInformation(List<UserFieldValue> existingDocumentValues, List<UserFieldValue> newDocumentValues) {
        for (UserFieldValue newValue : newDocumentValues) {

            UserFieldValue existingValue = existingDocumentValues.stream()
                    .filter(value -> value.getField().getId() == newValue.getField().getId()
                            && value.getSourceOfData() == newValue.getSourceOfData())
                    .findFirst()
                    .orElse(null);

            if (existingValue == null) {

                userFieldValueRepository.save(newValue);
            } else if (newValue.getConfidence() > existingValue.getConfidence() && !newValue.getValue().equals(existingValue.getValue())) {

                existingValue.setValue(newValue.getValue());
                existingValue.setConfidence(newValue.getConfidence());
                userFieldValueRepository.save(existingValue);
            }
        }
        //TODO unele date se schimba de la un document la altul
    }



    private String extractFieldValue(List<UserFieldValue> values) {
        return values.stream()
                .filter(v -> v.getField().getFieldName().equals("Document Issue Date"))
                .map(UserFieldValue::getValue)
                .findFirst()
                .orElse(null);
    }

    private boolean isDateNewer(String dateStr1, String dateStr2) {
        if (dateStr1 == null) return false;
        if (dateStr2 == null) return true;

        try {

            LocalDate date1 = LocalDate.parse(dateStr1);
            LocalDate date2 = LocalDate.parse(dateStr2);
            return date1.isAfter(date2);
        } catch (DateTimeParseException e) {

            return false;
        }
    }
}