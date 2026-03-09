package com.example.signing;

import com.example.certificate.CertificationAuthority;
import com.example.dto.SignatureInfo;
import com.example.security.SignaturePort;
import eu.europa.esig.dss.alert.SilentOnStatusAlert;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignerTextPosition;
import eu.europa.esig.dss.enumerations.TextWrapping;
import eu.europa.esig.dss.model.*;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.SignatureFieldParameters;
import eu.europa.esig.dss.pades.SignatureImageParameters;
import eu.europa.esig.dss.pades.SignatureImageTextParameters;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.spi.x509.CommonTrustedCertificateSource;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;
import eu.europa.esig.dss.xml.common.SchemaFactoryBuilder;
import eu.europa.esig.dss.xml.common.XmlDefinerUtils;
import lombok.AllArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Component
@AllArgsConstructor
public class DigitalSignature implements SignaturePort {
    private final CertificationAuthority certificationAuthority;
    static {

        SchemaFactoryBuilder schemaFactoryBuilder = SchemaFactoryBuilder.getSecureSchemaBuilder();
        schemaFactoryBuilder.setSecurityExceptionAlert(new SilentOnStatusAlert());
        XmlDefinerUtils.getInstance().setSchemaFactoryBuilder(schemaFactoryBuilder);
    }

    public void signDocument(String userCertificatePath, String certificatePassword, String pdfPath, String outputFile,int singerNumber) {
        try (Pkcs12SignatureToken token = new Pkcs12SignatureToken(new FileInputStream(userCertificatePath), new KeyStore.PasswordProtection(certificatePassword.toCharArray()))) {

            List<DSSPrivateKeyEntry> keys = token.getKeys();
            DSSPrivateKeyEntry privateKey = keys.getFirst();

            PAdESSignatureParameters parameters = new PAdESSignatureParameters();
            parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
            parameters.setSigningCertificate(privateKey.getCertificate());
            parameters.setCertificateChain(privateKey.getCertificateChain());


            SignatureImageParameters imageParameters = new SignatureImageParameters();
            SignatureFieldParameters fieldParameters = new SignatureFieldParameters();
            fieldParameters.setFieldId("Signature" + singerNumber); // campul pre-creat
            imageParameters.setFieldParameters(fieldParameters);



            SignatureImageTextParameters textParameters = new SignatureImageTextParameters();
            textParameters.setText(
                    "Signed by: " + getCommonName(privateKey) + "\n" +
                            "Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
            );
            textParameters.setTextWrapping(TextWrapping.FILL_BOX_AND_LINEBREAK);
            textParameters.setSignerTextPosition(SignerTextPosition.LEFT);
            imageParameters.setTextParameters(textParameters);

            parameters.setImageParameters(imageParameters);



            // Add trusted CA certificate
            CommonTrustedCertificateSource trustedCertSource = new CommonTrustedCertificateSource();
            X509Certificate caCert = certificationAuthority.loadCertificate();
            trustedCertSource.addCertificate(DSSUtils.loadCertificate(caCert.getEncoded()));

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

    public List<SignatureInfo> verifySignatures(InputStream signedPdf) {
        try {
            byte[] pdfBytes = DSSUtils.toByteArray(signedPdf);
            DSSDocument document = new InMemoryDocument(pdfBytes);

            SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(document);

            CommonTrustedCertificateSource trustedSource = new CommonTrustedCertificateSource();
            X509Certificate caCert = certificationAuthority.loadCertificate();
            trustedSource.addCertificate(DSSUtils.loadCertificate(caCert.getEncoded()));

            CommonCertificateVerifier certificateVerifier = new CommonCertificateVerifier();
            certificateVerifier.setTrustedCertSources(trustedSource);

            certificateVerifier.setCheckRevocationForUntrustedChains(false);
            validator.setCertificateVerifier(certificateVerifier);

            Reports reports = validator.validateDocument();
            SimpleReport report = reports.getSimpleReport();

            List<SignatureInfo> signatureInfos = new ArrayList<>();

            for (String sigId : report.getSignatureIdList()) {
                String name = report.getSignedBy(sigId);
                java.util.Date date = report.getSigningTime(sigId);

                boolean isValid = !eu.europa.esig.dss.enumerations.Indication.FAILED.equals(report.getIndication(sigId));


                signatureInfos.add(new SignatureInfo(name, date,isValid));
            }

            return signatureInfos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Eroare la verificarea semnăturii PDF-ului", e);
        }
    }

    public void prepareForSigning(String pdfPath, int numberOfSigners) {
        String tempPath = pdfPath + ".tmp";

        try (PDDocument pdDoc = PDDocument.load(new File(pdfPath))) {

            CommonCertificateVerifier verifier = new CommonCertificateVerifier();
            verifier.setCheckRevocationForUntrustedChains(false);
            PAdESService service = new PAdESService(verifier);

            PDPage lastPage = pdDoc.getPage(pdDoc.getNumberOfPages() - 1);
            float pageWidth  = lastPage.getMediaBox().getWidth();
            float pageHeight = lastPage.getMediaBox().getHeight();

            float sigWidth      = 200;
            float sigHeight     = 60;
            float marginSide    = 20;
            float marginBetween = 15;
            float marginTop     = 20;

            int targetPage = pdDoc.getNumberOfPages();
            int sigsPerRow = (int) ((pageWidth - 2 * marginSide) / (sigWidth + marginBetween));

            DSSDocument result = new FileDocument(pdfPath); // ← inițializare

            for (int i = 0; i < numberOfSigners; i++) {
                int col = i % sigsPerRow;
                int row = i / sigsPerRow;

                float x = marginSide + col * (sigWidth + marginBetween);
                float y = pageHeight - marginTop - sigHeight - row * (sigHeight + marginBetween);

                SignatureFieldParameters field = new SignatureFieldParameters();
                field.setFieldId("Signature" + (i + 1));
                field.setOriginX(x);
                field.setOriginY(y);
                field.setWidth(sigWidth);
                field.setHeight(sigHeight);
                field.setPage(targetPage);

                result = service.addNewSignatureField(result, field);
            }

            result.save(tempPath); // ← temp, nu pdfPath direct

        } catch (Exception e) {
            throw new RuntimeException("Eroare la pregatirea documentului", e);
        }

        // Înlocuiești după ce PDDocument e închis
        try {
            Files.move(
                    Path.of(tempPath),
                    Path.of(pdfPath),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException("Eroare la salvarea documentului", e);
        }
    }



        private String getCommonName(DSSPrivateKeyEntry privateKey) {
            String dn = privateKey.getCertificate().getSubject().getPrincipal().toString();
            for (String part : dn.split(",")) {
                if (part.trim().startsWith("CN=")) {
                    return part.trim().substring(3);
                }
            }
            return dn;
        }
}
