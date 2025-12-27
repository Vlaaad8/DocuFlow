package com.example.converter;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.docx4j.Docx4J;
import org.docx4j.Docx4jProperties;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.fonts.BestMatchingMapper;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Entities;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

    public static String convertWordToHTML(byte[] document) throws Exception {
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new ByteArrayInputStream(document));

        Docx4jProperties.setProperty("docx4j.Convert.Out.HTML.OutputMethodXML", true);

        HTMLSettings settings = Docx4J.createHTMLSettings();
        settings.setWmlPackage(wordMLPackage);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Docx4J.toHTML(settings, out, Docx4J.FLAG_EXPORT_PREFER_XSL);
            return out.toString(StandardCharsets.UTF_8);
        } catch (IOException | Docx4JException e) {
            throw new RuntimeException(e);
        }
    }
    public static void convertHTMLToWord(String html,Path outputDOCX) throws Exception{
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
        XHTMLImporterImpl importer = new XHTMLImporterImpl(wordMLPackage);
        String baseUri = null;

        wordMLPackage.getMainDocumentPart().getContent().addAll(importer.convert(toXhtml(html), baseUri));
        try (OutputStream os = Files.newOutputStream(outputDOCX)) {
            wordMLPackage.save(os);
        }
    }
    private static String toXhtml(String html) {
        org.jsoup.nodes.Document doc = Jsoup.parseBodyFragment(html);
        doc.outputSettings()
                .syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml)
                .escapeMode(Entities.EscapeMode.xhtml)
                .charset(StandardCharsets.UTF_8)
                .prettyPrint(false);

        return doc.body().html();
    }

}
