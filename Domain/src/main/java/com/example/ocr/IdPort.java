package com.example.ocr;

import java.io.InputStream;
import java.util.List;

public interface IdPort {
    public List<ExtractedField> analyzeId(InputStream fileStream, long fileLength);
}
