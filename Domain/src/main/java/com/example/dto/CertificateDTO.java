package com.example.dto;

public record CertificateDTO(String cn,String email,String city,String ou,String issuer,String serialHex,String validFrom,String validTo,int daysLeft) {
}
