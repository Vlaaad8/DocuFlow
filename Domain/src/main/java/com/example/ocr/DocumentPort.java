package com.example.ocr;

import java.io.InputStream;
import java.util.List;

public interface DocumentPort {
    public List<ExtractedField> extractText(InputStream fileStream, long fileLength);
}
