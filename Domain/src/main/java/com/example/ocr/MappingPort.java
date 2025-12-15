package com.example.ocr;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface MappingPort {

    void fillTemplate(Path templateDocx, Path outputDocx,Map<String,String> values) throws IOException;
}
