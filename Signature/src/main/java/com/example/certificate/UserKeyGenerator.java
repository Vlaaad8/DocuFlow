package com.example.certificate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;

public class UserKeyGenerator {

    private KeyPair keyPair;

    public UserKeyGenerator() {
        this.initializeKeyPair();
    }

    public PublicKey getPublicKey() {
        return this.keyPair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return this.keyPair.getPrivate();
    }

    private void initializeKeyPair() {
        Security.addProvider(new BouncyCastleProvider());
        try {
            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA", "BC");
            keyGenerator.initialize(2048, new SecureRandom());
            keyPair = keyGenerator.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
