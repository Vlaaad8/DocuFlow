package com.example.generator;

import com.example.converter.Convertors;
import com.example.ocr.MappingPort;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;


//TODO uneori nu mapeaza bine, to verify
@Component
public class ApachePOI implements MappingPort {

    @Override
    public void fillTemplate(Path templateDocx, Path outputDocx, Map<String, String> values) throws IOException {
        try (InputStream stream = Files.newInputStream(templateDocx)) {

            XWPFDocument document = new XWPFDocument(stream);
            replaceInBody(document, values);

            for (XWPFHeader header : document.getHeaderList()) {
                replaceInParagraphs(header.getParagraphs(), values);
                replaceInTables(header.getTables(), values);
            }
            for (XWPFFooter footer : document.getFooterList()) {
                replaceInParagraphs(footer.getParagraphs(), values);
                replaceInTables(footer.getTables(), values);
            }

            Convertors.convertWordToPDF(document, outputDocx);

        } catch (Exception  e) {
            throw new RuntimeException(e);
        }
    }


    private static void replaceInBody(XWPFDocument doc, Map<String, String> values) {
        replaceInParagraphs(doc.getParagraphs(), values);
        replaceInTables(doc.getTables(), values);
    }

    private static void replaceInTables(List<XWPFTable> tables, Map<String, String> values) {
        for (XWPFTable table : tables) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    replaceInParagraphs(cell.getParagraphs(), values);
                    replaceInTables(cell.getTables(), values);
                }
            }
        }
    }

    private static void replaceInParagraphs(List<XWPFParagraph> paragraphs, Map<String, String> values) {
        for (XWPFParagraph p : paragraphs) {
            replaceInParagraph(p, values);
        }
    }

    private static void replaceInParagraph(XWPFParagraph paragraph, Map<String, String> values) {
        String paragraphText = paragraph.getText();


        boolean updated = false;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            if (paragraphText.contains(entry.getKey())) {
                paragraphText = paragraphText.replace(entry.getKey(), Objects.toString(entry.getValue(), ""));
                updated = true;
            }
        }

        if (updated) {

            List<XWPFRun> runs = paragraph.getRuns();
            for (int i = runs.size() - 1; i >= 0; i--) {
                paragraph.removeRun(i);
            }
            XWPFRun newRun = paragraph.createRun();
            newRun.setText(paragraphText);
        }
    }

}
