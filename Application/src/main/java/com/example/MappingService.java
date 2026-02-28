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

@AllArgsConstructor
@Service
public class MappingService {

    private final FieldMapperRepository fieldMapperRepository;
    private final UserRepository userRepository;
    private final UserFieldValueRepository userFieldValueRepository;

    public void mapField(ExtractedField azureField, int userId) {

        Field field = fieldMapperRepository.getByAzureFieldName(azureField.getLabel()).orElseThrow(() -> new FieldException("No mapping found for Azure field: " + azureField.getLabel()));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(userId));

        UserFieldValue userFieldValue = userFieldValueRepository.findByUser_IdAndField_id(user.getId(), field.getId()).orElse(null);
        if (userFieldValue!= null) {
            userFieldValue.setValue(azureField.getValue());
            userFieldValue.setSourceOfData(mapSourceOfData(azureField.getSourceOfData()));

        } else {

            userFieldValue = new UserFieldValue(azureField.getValue(), field, user, mapSourceOfData(azureField.getSourceOfData()));
        }
        userFieldValueRepository.save(userFieldValue);

    }

    private SourceOfData mapSourceOfData(String source) {
        switch (source) {
            case ("idDocument.nationalIdentityCard"):
                return SourceOfData.NATIONAL_IDENTITY_CARD;
            case ("idDocument.passport"):
                return SourceOfData.PASSPORT;
            case ("idDocument.driverLicense"):
                return SourceOfData.DRIVER_LICENSE;
            case ("idDocument.residencePermit"):
                return SourceOfData.RESIDENCE_PERMIT;
            case ("idDocument.usSocialSecurityCard"):
                return SourceOfData.SOCIAL_SECURITY_CARD;
            case ("manualEntry"):
                return SourceOfData.MANUAL_ENTRY;
            default:
                return SourceOfData.UNKNOWN;
        }
    }
}
