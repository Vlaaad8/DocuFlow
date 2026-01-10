package com.example.security;

import java.io.InputStream;

public interface SignaturePort {
   void signDocument(String userCertificatePath, String certificatePassword, String pdfPath, String outputFile);
}
