package com.example.security;

import com.example.dto.CertificateDTO;

import java.nio.file.Path;
import java.security.KeyStoreException;

public interface CertificatePort {
    String issueCertificate(String firstName, String lastName, String email, String role,int userID) throws Exception;
    public CertificateDTO readCertificate(Path path, char[] password) throws KeyStoreException;
}
