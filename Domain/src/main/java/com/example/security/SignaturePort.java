package com.example.security;

import com.example.dto.SignatureInfo;

import java.io.InputStream;
import java.util.List;

public interface SignaturePort {
   void signDocument(String userCertificatePath, String certificatePassword, String pdfPath, String outputFile);
   public List<SignatureInfo> verifySignatures(InputStream signedPdf);
}
