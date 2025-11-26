package com.example;

import com.example.ocr.DocumentPort;
import com.example.ocr.ExtractedField;
import com.example.ocr.IdPort;

import java.io.InputStream;
import java.util.List;


public class IdService {

    private final IdPort idPort;

    public IdService(IdPort idPort) {
        this.idPort = idPort;
    }

    public List<ExtractedField> getFields(InputStream inputStream , int fileSize) {
        return idPort.analyzeId(inputStream, fileSize);
    }

//Start Validare CNP https://ro.wikipedia.org/wiki/Cod_numeric_personal_(România)
    private boolean validateCNP(String cnp) {
        if(cnp.length() != 13) {
            return false;
        }
        return true;
    }
}
