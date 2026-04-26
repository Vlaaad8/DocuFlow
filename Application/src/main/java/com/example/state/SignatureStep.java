package com.example.state;


import com.example.approval.ApprovalStatus;
import com.example.security.SignaturePort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SignatureStep implements RequestStep {

    private final SignaturePort signaturePort;

    private static final String CERTIFICATES_BASE_PATH = "storage/security/certificates/user_";

    @Override
    public void execute(RequestContext context, Runnable next) {

        if(context.getAnswer() == ApprovalStatus.ACCEPTED) {
            String path = context.getApproval().getApprovalRequest().getTemplate().getPath();
            try {
                this.signaturePort.signDocument(CERTIFICATES_BASE_PATH + context.getApproverId() + ".p12", "parola", path, context.getApproval().getApprovalRequest().getCurrentStep());

            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        next.run();

    }
}
