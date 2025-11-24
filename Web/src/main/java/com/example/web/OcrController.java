package com.example.web;


import com.example.DocumentService;
import com.example.IdService;
import com.example.ocr.ExtractedField;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class OcrController {

    private final IdService idService;
    private final DocumentService documentService;

    @PostMapping("ocr/identity-card")
    public List<ExtractedField> extractDataId(@RequestParam MultipartFile file) throws IOException {
        return idService.getFields(file.getInputStream(), (int) file.getSize());
    }

    @PostMapping("ocr/document")
    public List<ExtractedField> extractDataDocument(@RequestParam MultipartFile file) throws IOException {
        return  documentService.getKeyValues(file.getInputStream(), (int) file.getSize());
    }
}
