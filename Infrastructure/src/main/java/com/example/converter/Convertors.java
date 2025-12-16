package com.example.converter;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.docx4j.Docx4J;
import org.docx4j.fonts.BestMatchingMapper;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

public class Convertors {

    public static void convertWordToPDF(XWPFDocument document, Path outputPDF) throws Exception {

        byte[] docxBytes;
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            document.write(baos);
            docxBytes = baos.toByteArray();
        }


        WordprocessingMLPackage wordMLPackage;
        try(InputStream is = new ByteArrayInputStream(docxBytes)){
            wordMLPackage = WordprocessingMLPackage.load(is);
        }
        wordMLPackage.setFontMapper(new BestMatchingMapper());
        try(OutputStream os = new FileOutputStream(outputPDF.toFile())){
            Docx4J.toPDF(wordMLPackage, os);
        }
    }
}
