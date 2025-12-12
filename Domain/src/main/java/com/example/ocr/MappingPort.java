package com.example.ocr;

import java.util.List;

public interface MappingPort {

    public void map(List<ExtractedField> extractedFieldList, int userID);
}
