package com.example.web;


import com.example.CreatorService;
import com.example.template.Field;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200/")
public class CreatorController {

    private final CreatorService creatorService;

    @GetMapping("creator/field")
    public List<Field> getFields(){
        return this.creatorService.getAllFields();
    }
}
