package com.example.converter;


import com.example.ConvertPort;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.docx4j.Docx4J;
import org.docx4j.Docx4jProperties;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.fonts.BestMatchingMapper;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Entities;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class Convertors implements ConvertPort {
    static {
        System.setProperty("docx4j.jaxb.Context", "org.docx4j.jaxb.Context");
        org.docx4j.Docx4jProperties.setProperty("docx4j.fonts.RunFontSelector.DefaultFont", "Times New Roman");
    }
    public static void convertWordToPDF(XWPFDocument document, Path outputPDF) throws Exception {

        byte[] docxBytes;
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            document.write(baos);
            docxBytes = baos.toByteArray();
        }

        XWPFParagraph p = document.createParagraph();
        p.createRun().addBreak(BreakType.PAGE);

        WordprocessingMLPackage wordMLPackage;
        try(InputStream is = new ByteArrayInputStream(docxBytes)){
            wordMLPackage = WordprocessingMLPackage.load(is);
        }

        wordMLPackage.setFontMapper(FontManager.getInstance());
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
            String html = out.toString(StandardCharsets.UTF_8);
            html = StringEscapeUtils.unescapeHtml4(html);

            html = html.replaceAll("(?i)</?o:[^>]*>", "");

            org.jsoup.nodes.Document doc = Jsoup.parse(html);

            return doc.body().html();

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

    @Override
    public void convertWordToPDF(String path) throws Exception {
        XWPFDocument document;
        try (InputStream stream = Files.newInputStream(Path.of(path))) {
            document = new XWPFDocument(stream);
        }
        convertWordToPDF(document, Path.of(path.replace(".docx", ".pdf")));
    }
}
