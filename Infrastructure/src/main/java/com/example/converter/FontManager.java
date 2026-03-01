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
                return instance;
            }
        }
        return instance;
    }

    private static BestMatchingMapper initializeMapper() throws Exception {
        PhysicalFonts.discoverPhysicalFonts();

        instance = new BestMatchingMapper();
        PhysicalFont timesFont = PhysicalFonts.get("Times New Roman");

        if (timesFont != null) {

            instance.put("Times New Roman", timesFont);
            instance.put("Times-Roman", timesFont);
            instance.put("serif", timesFont);
            instance.put("Arial", timesFont);
            instance.put("Calibri", timesFont);
            instance.put("Aptos", timesFont);
        }
        return instance;
    }
}
