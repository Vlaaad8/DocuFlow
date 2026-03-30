package com.example;

import com.example.dto.CertificateDTO;
import com.example.dto.SignatureInfo;
import com.example.dto.UserFieldValueDTO;
import com.example.dto.UserSavedValueDTO;
import com.example.jpa.UserFieldValueRepository;
import com.example.ocr.DocumentPort;
import com.example.security.CertificatePort;
import com.example.security.SignaturePort;
import com.example.template.SourceOfData;
import com.example.template.UserFieldValue;
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
    private UserFieldValueRepository userFieldValueRepository;

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

    public List<UserSavedValueDTO> getSavedData(int userID) {
       return  this.userFieldValueRepository.findAllByUser_Id(userID)
                .stream().map(
                        userFieldValue ->
                                new UserSavedValueDTO(userFieldValue.getId(), userFieldValue.getValue(), userFieldValue.getSourceOfData().toString(), userFieldValue.getField().getFieldName(), userFieldValue.getUser().getId())

                ).toList();

    }

    public void updateField(int fieldID, String value) {
        UserFieldValue userFieldValue = this.userFieldValueRepository.findById(fieldID).orElseThrow(() -> new RuntimeException("Field not found"));
        userFieldValue.setValue(value);
        userFieldValue.setSourceOfData(SourceOfData.MANUAL_ENTRY);
        this.userFieldValueRepository.save(userFieldValue);
    }
}