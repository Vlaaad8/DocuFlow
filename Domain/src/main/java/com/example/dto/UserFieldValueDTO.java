package com.example.dto;

import com.example.template.SourceOfData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserFieldValueDTO {
    String name;
    String value;
    SourceOfData sourceOfData;
}
