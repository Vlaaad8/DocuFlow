package com.example.web;

import com.example.GeneratorService;
import com.example.dto.FieldValueDTO;
import com.example.dto.GeneratorTemplateDTO;
import lombok.AllArgsConstructor;
import oracle.jdbc.proxy.annotation.Post;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200/")
public class GenerateController {

    private final GeneratorService generatorService;

    @PostMapping("generate")
    public void generateTemplate(@RequestParam("userID") int userID, @RequestParam("templateID") int templateID ) {
        this.generatorService.generateFile(templateID,userID);
    }

    @GetMapping("generate/{userID}")
    public List<GeneratorTemplateDTO> getTemplates(@PathVariable("userID") int userID) {
        return this.generatorService.giveTemplatesWithLock(userID);
    }
    @GetMapping("generate/{templateId}/{userId}")
    public List<FieldValueDTO> getFields(@PathVariable("templateId") int templateId, @PathVariable("userId") int userID) {
        return this.generatorService.getTemplateValues(templateId,userID);
    }
}
