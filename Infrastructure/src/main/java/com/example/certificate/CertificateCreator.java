package com.example.certificate;


import com.example.dto.CertificateDTO;
import com.example.security.CertificatePort;
import lombok.AllArgsConstructor;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Component;

import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@AllArgsConstructor
public class CertificateCreator implements CertificatePort {

    private final CertificationAuthority certificationAuthority;

    @Override
    public String issueCertificate(String firstName, String lastName, String email, String role, int userID) throws Exception {
        UserKeyGenerator userKeyGenerator = new UserKeyGenerator();

        PrivateKey privateKey = userKeyGenerator.getPrivateKey();
        PublicKey publicKey = userKeyGenerator.getPublicKey();

        PrivateKey certificationPrivateKey = certificationAuthority.loadPrivateKey();
        X509Certificate certificate = certificationAuthority.loadCertificate();

        X500NameBuilder nameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        nameBuilder.addRDN(BCStyle.CN, firstName + " " + lastName);
        nameBuilder.addRDN(BCStyle.E, email);
        nameBuilder.addRDN(BCStyle.OU, role);


        X500Name subject = nameBuilder.build();
        Date notBefore = new Date();
        Date notAfter = new Date(System.currentTimeMillis() + (2L * 365 * 24 * 60 * 60 * 1000));
        //TODO modify serial number
        BigInteger serialNumber = BigInteger.valueOf(userID);

        X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(
                certificate,
                serialNumber,
                notBefore,
                notAfter,
                subject,
                publicKey
        );
        ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption").setProvider("BC").build(certificationPrivateKey);

        X509Certificate userCertificate = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certificateBuilder.build(signer));

        return saveToPKSC12(userID, privateKey, userCertificate, certificate);
    }

    private String saveToPKSC12(int userID, PrivateKey privateKey, X509Certificate userCertificate, X509Certificate authorityCertificate) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
        keyStore.load(null);

        X509Certificate[] chain = new X509Certificate[]{userCertificate, authorityCertificate};

        //TODO to modify this , i need a strong and reliable password
        String password = "parola";
        keyStore.setKeyEntry("user-signature", privateKey, password.toCharArray(), chain);
        Path output = Paths.get("storage/security/certificates/user_" + userID + ".p12");
        Files.createDirectories(output.getParent());

        try (FileOutputStream fos = new FileOutputStream(output.toFile())) {
            keyStore.store(fos, password.toCharArray());
        }
        return output.toString();
    }

    public CertificateDTO readCertificate(Path path, char[] password) throws KeyStoreException {
        KeyStore ks = KeyStore.getInstance("PKCS12");

        try (InputStream inputStream = Files.newInputStream(path)) {
            ks.load(inputStream, password);
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new RuntimeException(e);
        }

        X509Certificate certificate = (X509Certificate) ks.getCertificate("user-signature");

        String subject = certificate.getSubjectX500Principal().getName("RFC2253");
        String issuer = certificate.getIssuerX500Principal().getName("RFC2253");

        String issuerName =getAttribute(issuer,"CN");
        String issuerCity = getAttribute(issuer,"L");
        String cn = getAttribute(subject, "CN");
        String email = getAttribute(subject, "E");

        String ou = getAttribute(subject, "OU");

        Instant from = certificate.getNotBefore().toInstant();
        Instant to = certificate.getNotAfter().toInstant();
        int daysLeft = (int) ChronoUnit.DAYS.between(Instant.now(), to);
        String serialHex = certificate.getSerialNumber().toString(16).toUpperCase();

        return new CertificateDTO(cn, email,issuerCity, ou, issuerName, serialHex, from.toString().split("T")[0], to.toString().split("T")[0], daysLeft);
    }

    private static String getAttribute(String dn, String attr) {
        try {
            for (Rdn rdn : new LdapName(dn).getRdns()) {
                if (rdn.getType().equalsIgnoreCase(attr)) {
                    return String.valueOf(rdn.getValue());
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
