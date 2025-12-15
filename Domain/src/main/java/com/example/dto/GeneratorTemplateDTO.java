package com.example.dto;

import com.example.template.Template;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GeneratorTemplateDTO {
    private Template template;
    private boolean canGenerate;
}
