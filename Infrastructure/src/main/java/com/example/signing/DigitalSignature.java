package com.example.signing;

import com.example.dto.SignatureInfo;
import com.example.security.SignaturePort;
import eu.europa.esig.dss.alert.handler.AlertHandler;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.*;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.policy.ValidationPolicyFacade;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

@Component
public class DigitalSignature implements SignaturePort {
    static {
        System.setProperty("javax.xml.accessExternalDTD", "all");
        System.setProperty("javax.xml.accessExternalSchema", "all");
    }

    public void signDocument(String userCertificatePath, String certificatePassword, String pdfPath, String outputFile) {
        try (Pkcs12SignatureToken token = new Pkcs12SignatureToken(new FileInputStream(userCertificatePath), new KeyStore.PasswordProtection(certificatePassword.toCharArray()))) {

            List<DSSPrivateKeyEntry> keys = token.getKeys();

            DSSPrivateKeyEntry privateKey = keys.getFirst();

            PAdESSignatureParameters parameters = new PAdESSignatureParameters();

            parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
            parameters.setSigningCertificate(privateKey.getCertificate());
            parameters.setCertificateChain(privateKey.getCertificateChain());

            CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier();
            commonCertificateVerifier.setCheckRevocationForUntrustedChains(false);
            PAdESService service = new PAdESService(commonCertificateVerifier);

            DSSDocument toSignDocument = new FileDocument(pdfPath);

            ToBeSigned dataToSign = service.getDataToSign(toSignDocument, parameters);

            SignatureValue signatureValue = token.sign(dataToSign, parameters.getDigestAlgorithm(), privateKey);

            DSSDocument signedDocument = service.signDocument(toSignDocument, parameters, signatureValue);

            signedDocument.save(outputFile);
        } catch (Exception e) {
            throw new RuntimeException("Eroare la semnarea PDF-ului", e);
        }
    }

    static {
        // Dezactivează restricțiile care cauzează SAXNotRecognizedException în medii cu biblioteci mixte
        System.setProperty("javax.xml.accessExternalDTD", "all");
        System.setProperty("javax.xml.accessExternalSchema", "all");
    }

    public List<SignatureInfo> verifySignatures(InputStream signedPdf) {
        try {
            byte[] pdfBytes = DSSUtils.toByteArray(signedPdf);
            DSSDocument document = new InMemoryDocument(pdfBytes);

            SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(document);

            CommonCertificateVerifier certificateVerifier = new CommonCertificateVerifier();
            // Important pentru licență (certificatele tale nu sunt în Trusted List-ul UE)
            certificateVerifier.setCheckRevocationForUntrustedChains(false);
            validator.setCertificateVerifier(certificateVerifier);

            // Rulăm validarea
            Reports reports = validator.validateDocument();
            SimpleReport report = reports.getSimpleReport();

            List<SignatureInfo> signatureInfos = new ArrayList<>();
            // Formatează data pentru DTO-ul tău (presupunând că SignatureInfo acceptă String sau Date)
            for (String sigId : report.getSignatureIdList()) {
                String name = report.getSignedBy(sigId);
                java.util.Date date = report.getSigningTime(sigId);

                // Creează și ADĂUGĂ în listă
                signatureInfos.add(new SignatureInfo(name, date));
            }

            return signatureInfos;
        } catch (Exception e) {
            e.printStackTrace(); // Vezi în consolă dacă eroarea se schimbă
            throw new RuntimeException("Eroare la verificarea semnăturii PDF-ului", e);
        }
    }
}
