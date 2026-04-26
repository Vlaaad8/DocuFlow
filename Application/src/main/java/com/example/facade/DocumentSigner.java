package com.example.facade;

import com.example.approval.ApprovalChain;
import com.example.security.SignaturePort;
import com.example.template.FilledTemplate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;


@AllArgsConstructor
@Component
public class DocumentSigner {
    private final SignaturePort signaturePort;
    private static final String CERTIFICATES_BASE_PATH = "storage/security/certificates/user_";

    public void prepareAndSign(FilledTemplate filledTemplate, int userId) throws Exception {
        String pdfPath = filledTemplate.getPath();
        int numberOfSigners = filledTemplate.getTemplate()
                .getApprovalChain().getSteps().size();

        signaturePort.prepareForSigning(pdfPath, numberOfSigners);

        String certPath = CERTIFICATES_BASE_PATH + userId + ".p12";
        signaturePort.signDocument(certPath, "parola", pdfPath, 0);
    }
}
