package com.example;

import com.example.jpa.FieldMapperRepository;
import com.example.jpa.UserFieldValueRepository;
import com.example.jpa.UserRepository;
import com.example.login.User;
import com.example.ocr.ExtractedField;
import com.example.template.Field;
import com.example.template.UserFieldValue;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class MappingService {

    private final FieldMapperRepository fieldMapperRepository;
    private final UserRepository userRepository;
    private final UserFieldValueRepository userFieldValueRepository;

    public void mapField(ExtractedField azureField, int userId) {

        Field field = fieldMapperRepository.getByAzureFieldName(azureField.getLabel()).orElse(null);
        System.out.println(azureField);
        User user = userRepository.findById(userId).orElse(null);
        if (field != null && user != null) {
            UserFieldValue userFieldValue = userFieldValueRepository.findByUser_IdAndField_id(user.getId(),field.getId()).orElse(null);
            if (userFieldValue!=null){
                userFieldValue.setValue(azureField.getValue());
            }
            else {
                userFieldValue = new UserFieldValue(azureField.getValue(), field, user);
            }
            userFieldValueRepository.save(userFieldValue);
        }
    }
}
