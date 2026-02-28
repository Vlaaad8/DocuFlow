package com.example.web;

import com.example.GeneratorService;
import com.example.dto.DataProfileResponse;
import com.example.dto.Generate.GeneratorTemplateApproverDTO;
import com.example.dto.UserFieldValueDTO;
import com.example.dto.Generate.GeneratorTemplateDTO;
import com.example.template.SourceOfData;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Source;
import java.util.List;
import java.util.Map;

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
    public List<UserFieldValueDTO> getFields(@PathVariable("templateId") int templateId, @PathVariable("userId") int userID) {
        return this.generatorService.getTemplateValues(templateId,userID);
    }
    @GetMapping("generate/approver/{templateId}/{userId}")
    public List<GeneratorTemplateApproverDTO> getApproverTemplates(@PathVariable("templateId") int templateId, @PathVariable("userId") int userID) {
        return this.generatorService.getApprovesForTemplate(templateId,userID);
    }

    @GetMapping("generate/profile/{userId}")
    public Map<SourceOfData,Boolean> getDataProfile(@PathVariable("userId") int userID) {
        return this.generatorService.getDataProfile(userID);
    }
}
