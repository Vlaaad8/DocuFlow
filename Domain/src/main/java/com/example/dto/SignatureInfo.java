package com.example.dto;

public record SignatureInfo(
        String signerName,
        java.util.Date signingDate,
        boolean isValid
) {
}
