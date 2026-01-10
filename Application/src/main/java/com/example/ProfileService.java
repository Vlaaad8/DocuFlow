package com.example;

import com.example.dto.CertificateDTO;
import com.example.security.CertificatePort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.security.KeyStoreException;

@Service
@AllArgsConstructor
public class ProfileService {


    private CertificatePort certificatePort;

    public CertificateDTO getCertificateInfo(int userID) {
        Path certificate = Path.of("storage/security/certificates/user_" + userID + ".p12");
        try {
            return this.certificatePort.readCertificate(certificate, "parola".toCharArray());

        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }

    }
}
