package com.example.signing;

import com.example.security.SignaturePort;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.*;
import eu.europa.esig.dss.pades.DSSFont;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.SignatureImageTextParameters;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.spi.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class DigitalSignature implements SignaturePort {

    public void signDocument(String userCertificatePath, String certificatePassword, String pdfPath ,String outputFile){
        try(Pkcs12SignatureToken token = new Pkcs12SignatureToken(new FileInputStream(userCertificatePath),new KeyStore.PasswordProtection(certificatePassword.toCharArray()))){

            List<DSSPrivateKeyEntry> keys = token.getKeys();

            DSSPrivateKeyEntry privateKey= keys.getFirst();

            PAdESSignatureParameters parameters = new PAdESSignatureParameters();

            parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
            parameters.setSigningCertificate(privateKey.getCertificate());
            parameters.setCertificateChain(privateKey.getCertificateChain());

            CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier();
            PAdESService service = new PAdESService(commonCertificateVerifier);

            DSSDocument toSignDocument = new FileDocument(pdfPath);

//            SignatureImageTextParameters textParameters = new SignatureImageTextParameters();
//            textParameters.setText("Semnat digital de: " + firstName + " " + lastName + "\n" +
//                    "Rol: " + role + "\n" +
//                    "Data: " + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date()));
//            textParameters.setTextColor(Color.BLACK);
//            SignatureImageParameters imageParameters = new SignatureImageParameters();
//            imageParameters.setTextParameters(textParameters);

            ToBeSigned dataToSign = service.getDataToSign(toSignDocument,parameters);

            SignatureValue signatureValue = token.sign(dataToSign,parameters.getDigestAlgorithm(),privateKey);

            DSSDocument signedDocument = service.signDocument(toSignDocument, parameters, signatureValue);

            signedDocument.save("contract_semnat.pdf");
        } catch (Exception e) {
            throw new RuntimeException("Eroare la semnarea PDF-ului", e);
        }
    }
}
