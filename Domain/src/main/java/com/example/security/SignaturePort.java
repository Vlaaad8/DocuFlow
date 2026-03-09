package com.example.security;

import com.example.dto.SignatureInfo;

import java.io.InputStream;
import java.util.List;

public interface SignaturePort {
   void signDocument(String userCertificatePath, String certificatePassword, String pdfPath, String outputFile,int signerID) throws Exception;
   List<SignatureInfo> verifySignatures(InputStream signedPdf);
   void prepareForSigning(String pdfPath, int numberOfSigners);
}
