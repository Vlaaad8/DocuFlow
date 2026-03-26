package com.example.dto.Generate;

import com.example.dto.TemplateDTO;
import com.example.template.Template;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GeneratorTemplateDTO {
    private TemplateDTO template;
    private boolean canGenerate;
    private List<String> missingFields;
}
