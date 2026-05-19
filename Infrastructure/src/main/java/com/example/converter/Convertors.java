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
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Entities;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Convertors implements ConvertPort {
    static {
        System.setProperty("docx4j.jaxb.Context", "org.docx4j.jaxb.Context");
    }

    public static void convertWordToPDF(XWPFDocument document, Path outputPDF) throws Exception {
        byte[] docxBytes;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            document.write(baos);
            docxBytes = baos.toByteArray();
        }

        WordprocessingMLPackage wordMLPackage;
        try (InputStream is = new ByteArrayInputStream(docxBytes)) {
            wordMLPackage = WordprocessingMLPackage.load(is);
        }

        wordMLPackage.setFontMapper(FontManager.getInstance());
        try (OutputStream os = new FileOutputStream(outputPDF.toFile())) {
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
            org.jsoup.nodes.Element styleTag = doc.select("style").first();
            String defaultStyles = "";

            if (styleTag != null) {
                String css = styleTag.html();
                Pattern pattern = Pattern.compile("\\.([a-zA-Z0-9_-]+)\\s*\\{([^}]+)\\}");
                Matcher matcher = pattern.matcher(css);

                while (matcher.find()) {
                    String className = matcher.group(1);
                    String styleBody = matcher.group(2).trim();

                    if (className.equals("DocDefaults") || className.equals("Normal")) {
                        defaultStyles += styleBody + "; ";
                    }

                    for (org.jsoup.nodes.Element el : doc.select("." + className)) {
                        String existingStyle = el.attr("style");
                        if (!existingStyle.isEmpty() && !existingStyle.endsWith(";")) {
                            existingStyle += "; ";
                        }
                        el.attr("style", existingStyle + styleBody);
                        el.removeClass(className);
                    }
                }

                if (!defaultStyles.isEmpty()) {
                    doc.body().attr("style", defaultStyles + doc.body().attr("style"));
                }
            }

            return doc.body().html();

        } catch (IOException | Docx4JException e) {
            throw new RuntimeException(e);
        }
    }

    public static void convertHTMLToWord(String html, Path outputDOCX) throws Exception {
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
        MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();

        setDefaultFont(wordMLPackage, "Times New Roman");
        wordMLPackage.setFontMapper(FontManager.getInstance());

        XHTMLImporterImpl importer = new XHTMLImporterImpl(wordMLPackage);
        importer.setHyperlinkStyle("Hyperlink");

        String cleanHtml = normalizeHtmlFontsForDocx(html);

        String fullHtml = "<html><head></head><body>" + cleanHtml + "</body></html>";
        String xhtml = toXhtml(fullHtml);

        mdp.getContent().addAll(importer.convert(xhtml, null));

        try (OutputStream os = Files.newOutputStream(outputDOCX)) {
            wordMLPackage.save(os);
        }
    }

    private static String normalizeHtmlFontsForDocx(String html) {

        Pattern fontFamilyPattern = Pattern.compile("font-family\\s*:\\s*['\"]?([\\w\\s]+)['\"]?(?:\\s*,\\s*[^;\"']+)?");
        Matcher matcher = fontFamilyPattern.matcher(html);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String firstName = matcher.group(1).trim();
            String replacement = isSupportedFont(firstName)
                    ? "font-family: " + firstName
                    : "font-family: Times New Roman";
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        Pattern fontSizePattern = Pattern.compile("font-size\\s*:\\s*([\\d.]+)\\s*(pt|px|em|rem)?");
        Matcher sizeMatcher = fontSizePattern.matcher(result.toString());
        StringBuilder result2 = new StringBuilder();

        while (sizeMatcher.find()) {
            String value = sizeMatcher.group(1);
            String unit = sizeMatcher.group(2);
            String replacement;

            double ptValue;
            if (unit == null || unit.equals("pt")) {
                ptValue = Double.parseDouble(value);
                double px = ptValue * 1.333333;
                replacement = "font-size: " + Math.round(px) + "px";
            } else if (unit.equals("em") || unit.equals("rem")) {
                double em = Double.parseDouble(value);
                ptValue = em * 12;
                double px = ptValue * 1.333333;
                replacement = "font-size: " + Math.round(px) + "px";
            } else {
                replacement = sizeMatcher.group(0);
            }

            sizeMatcher.appendReplacement(result2, Matcher.quoteReplacement(replacement));
        }
        sizeMatcher.appendTail(result2);

        return result2.toString();
    }

    private static void setDefaultFont(WordprocessingMLPackage pkg, String fontName) throws Exception {
        org.docx4j.wml.RFonts rFonts = new org.docx4j.wml.RFonts();
        rFonts.setAscii(fontName);
        rFonts.setHAnsi(fontName);

        org.docx4j.wml.RPr rPr = new org.docx4j.wml.RPr();
        rPr.setRFonts(rFonts);

        org.docx4j.wml.DocDefaults.RPrDefault rPrDefault = new org.docx4j.wml.DocDefaults.RPrDefault();
        rPrDefault.setRPr(rPr);

        if (pkg.getMainDocumentPart().getStyleDefinitionsPart() != null &&
                pkg.getMainDocumentPart().getStyleDefinitionsPart().getContents() != null) {
            pkg.getMainDocumentPart().getStyleDefinitionsPart().getContents().getDocDefaults().setRPrDefault(rPrDefault);
        }
    }

    private static boolean isSupportedFont(String fontName) {
        return List.of("Times New Roman", "Arial", "Calibri", "Georgia", "Verdana", "Aptos", "Tahoma")
                .stream()
                .anyMatch(f -> f.equalsIgnoreCase(fontName));
    }

    private static String toXhtml(String html) {
        // Parse folosește string-ul complet (<html><body>), nu doar BodyFragment, ca să salvăm stilurile de bază
        org.jsoup.nodes.Document doc = Jsoup.parse(html);

        for (org.jsoup.nodes.Element table : doc.select("table")) {
            if (!table.hasAttr("border")) {
                table.attr("border", "1");
                table.attr("cellspacing", "0");
                table.attr("cellpadding", "4");
                table.attr("style", "border-collapse: collapse; " + table.attr("style"));
            }
        }

        doc.outputSettings()
                .syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml)
                .escapeMode(Entities.EscapeMode.xhtml)
                .charset(StandardCharsets.UTF_8)
                .prettyPrint(false);

        return doc.html();
    }

    @Override
    public void convertWordToPDF(String path) throws Exception {

        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new File(path));

        wordMLPackage.setFontMapper(FontManager.getInstance());


        Path outputPDF = Path.of(path.replace(".docx", ".pdf"));

        try (OutputStream os = Files.newOutputStream(outputPDF)) {
            Docx4J.toPDF(wordMLPackage, os);
        }
    }
}