package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TemplateDTO {
    private int id;
    private String name;
    private String category;
    private String description;
    private Set<FieldDTO> fields;
}
