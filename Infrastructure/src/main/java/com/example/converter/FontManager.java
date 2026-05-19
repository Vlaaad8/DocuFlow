package com.example.converter;

import org.docx4j.fonts.BestMatchingMapper;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;

public class FontManager {

    private static volatile BestMatchingMapper instance;


    private FontManager() {

    }

    public static BestMatchingMapper getInstance() {
        if (instance == null) {
            synchronized (FontManager.class) {
                if (instance == null) {
                    try {
                        instance = initializeMapper();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return instance;
    }

    private static BestMatchingMapper initializeMapper() throws Exception {
        PhysicalFonts.discoverPhysicalFonts();
        instance = new BestMatchingMapper();


        String[] popularFonts = {"Arial", "Calibri", "Georgia", "Verdana", "Helvetica"};
        PhysicalFont timesFont = PhysicalFonts.get("Times New Roman");


        if (timesFont != null) {
            instance.put("Times New Roman", timesFont);
            instance.put("Times-Roman", timesFont);
            instance.put("serif", timesFont);
        }

        for (String fontName : popularFonts) {
            PhysicalFont font = PhysicalFonts.get(fontName);
            if (font != null) {
                instance.put(fontName, font);
            } else if (timesFont != null) {

                instance.put(fontName, timesFont);
            }
        }

        PhysicalFont aptosFont = PhysicalFonts.get("Aptos");
        if (aptosFont != null) {
            instance.put("Aptos", aptosFont);
        } else if (timesFont != null) {
            instance.put("Aptos", timesFont);
        }

        return instance;
    }
}
