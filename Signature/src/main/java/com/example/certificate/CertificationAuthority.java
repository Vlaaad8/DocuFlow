package com.example.certificate;

import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

@Component
public class CertificationAuthority {

    private final Path workingDirectory = Paths.get("storage/security/certificationAuthority");
    @Value("${certification.authority.password}")
    private String password;


    public PrivateKey loadPrivateKey() throws Exception {
        try (PEMParser parser = new PEMParser(new FileReader(workingDirectory.resolve("domain.key").toFile()))) {
            Object object = parser.readObject();
            PKCS8EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = (PKCS8EncryptedPrivateKeyInfo) object;

            InputDecryptorProvider decrypt = new JceOpenSSLPKCS8DecryptorProviderBuilder().build(password.toCharArray());
            return new JcaPEMKeyConverter().getPrivateKey(encryptedPrivateKeyInfo.decryptPrivateKeyInfo(decrypt));
        }
    }

    public X509Certificate loadCertificate() throws Exception {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        try (FileInputStream fis = new FileInputStream(workingDirectory.resolve("CA.crt").toFile())) {
            return (X509Certificate) certificateFactory.generateCertificate(fis);
        }
    }


}
