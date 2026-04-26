package com.example.template;

public class FieldFlyweightImpl implements FieldFlyweight {


    private final int id;
    private final String fieldName;
    private final String representation;
    private final boolean required;

    public FieldFlyweightImpl(Field field) {
        this.id = field.getId();
        this.fieldName = field.getFieldName();
        this.representation = field.getRepresentation();
        this.required = field.isRequired();
    }

    @Override public int getId()               { return id; }
    @Override public String getFieldName()     { return fieldName; }
    @Override public String getRepresentation(){ return representation; }
    @Override public boolean isRequired()      { return required; }


    @Override
    public boolean existsIn(String textContent) {
        return textContent != null &&
                textContent.contains(this.representation);
    }
}