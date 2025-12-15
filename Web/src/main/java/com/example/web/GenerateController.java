package com.example.web;

import com.example.GeneratorService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200/")
public class GenerateController {

    private final GeneratorService generatorService;

    @PostMapping("generate")
    public void generateTemplate(@RequestParam("userID") int userID, @RequestParam("templateID")int templateID ) {
        this.generatorService.generateFile(userID, templateID);
    }

    @GetMapping("generate")
    public void getTemplatesWithStatus(){

    }
}
