package com.example;

import com.example.dto.CertificateDTO;
import com.example.dto.SignatureInfo;
import com.example.ocr.DocumentPort;
import com.example.security.CertificatePort;
import com.example.security.SignaturePort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Path;
import java.security.KeyStoreException;
import java.util.List;

@Service
@AllArgsConstructor
public class ProfileService {


    private CertificatePort certificatePort;
    private SignaturePort documentPort;

    public CertificateDTO getCertificateInfo(int userID) {
        Path certificate = Path.of("storage/security/certificates/user_" + userID + ".p12");
        try {
            return this.certificatePort.readCertificate(certificate, "parola".toCharArray());

        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }

    }

    public List<SignatureInfo> verifyDocument(InputStream document) {
        return this.documentPort.verifySignatures(document);
    }
}
