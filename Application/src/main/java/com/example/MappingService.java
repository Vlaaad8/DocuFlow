package com.example;

import com.example.jpa.FieldMapperRepository;
import com.example.jpa.UserFieldValueRepository;
import com.example.jpa.UserRepository;
import com.example.login.User;
import com.example.template.Field;
import com.example.template.UserFieldValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MappingService {

    private final FieldMapperRepository fieldMapperRepository;
    private final UserRepository userRepository;
    private final UserFieldValueRepository userFieldValueRepository;

    public void mapField(String azureField, int userId) {

        Field field = fieldMapperRepository.getByAzureFieldName(azureField).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        if (field != null && user != null) {
            UserFieldValue userFieldValue = new UserFieldValue(azureField, field, user);
            userFieldValueRepository.save(userFieldValue);
        }
        //TODO i saved the value introduced of user, now i have to continue with the actual mapping
    }
}
