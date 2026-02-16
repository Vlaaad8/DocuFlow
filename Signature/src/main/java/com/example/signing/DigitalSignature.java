package com.example.signing;

import com.example.certificate.CertificationAuthority;
import com.example.dto.SignatureInfo;
import com.example.security.SignaturePort;
import eu.europa.esig.dss.alert.SilentOnStatusAlert;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.*;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
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
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class DigitalSignature implements SignaturePort {
    private final CertificationAuthority certificationAuthority;
    static {
        // Configure SchemaFactoryBuilder to silently ignore security attribute errors
        SchemaFactoryBuilder schemaFactoryBuilder = SchemaFactoryBuilder.getSecureSchemaBuilder();
        schemaFactoryBuilder.setSecurityExceptionAlert(new SilentOnStatusAlert());
        XmlDefinerUtils.getInstance().setSchemaFactoryBuilder(schemaFactoryBuilder);
    }

    public void signDocument(String userCertificatePath, String certificatePassword, String pdfPath, String outputFile) {
        try (Pkcs12SignatureToken token = new Pkcs12SignatureToken(new FileInputStream(userCertificatePath), new KeyStore.PasswordProtection(certificatePassword.toCharArray()))) {

            List<DSSPrivateKeyEntry> keys = token.getKeys();
            DSSPrivateKeyEntry privateKey = keys.getFirst();

            PAdESSignatureParameters parameters = new PAdESSignatureParameters();
            parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
            parameters.setSigningCertificate(privateKey.getCertificate());
            parameters.setCertificateChain(privateKey.getCertificateChain());
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
                eu.europa.esig.dss.enumerations.Indication indication = report.getIndication(sigId);
                eu.europa.esig.dss.enumerations.SubIndication subIndication = report.getSubIndication(sigId);


                boolean isValid = !eu.europa.esig.dss.enumerations.Indication.FAILED.equals(report.getIndication(sigId));


                signatureInfos.add(new SignatureInfo(name, date,isValid));
            }

            return signatureInfos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Eroare la verificarea semnăturii PDF-ului", e);
        }
    }
}
