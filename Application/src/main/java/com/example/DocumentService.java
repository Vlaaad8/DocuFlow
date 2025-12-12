package com.example;

import com.example.ocr.DocumentPort;
import com.example.ocr.ExtractedField;
import lombok.AllArgsConstructor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

//TODO validari riguroase care sa urmareasca datele mele
@AllArgsConstructor
public class DocumentService {

    private final DocumentPort documentPort;

    /*
        Raw Data - has to be validated and formatted
     */
    public List<ExtractedField> getKeyValues(InputStream inputStream, int fileSize) {
        List<ExtractedField> formatedData = new ArrayList<>();
        return this.documentPort.extractText(inputStream, fileSize);
    }

}
