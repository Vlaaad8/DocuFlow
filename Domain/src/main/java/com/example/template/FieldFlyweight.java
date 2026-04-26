package com.example.template;

public interface FieldFlyweight {
    int getId();
    String getFieldName();
    String getRepresentation();
    boolean isRequired();

    boolean existsIn(String textContent);
}
