package com.example.facade;


import com.example.login.User;
import com.example.template.FilledTemplate;
import com.example.template.Template;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Map;

@Component
@AllArgsConstructor
public class GenerationFacade {
    private final DocumentFiller documentFiller;
    private final DocumentSigner documentSigner;
    private final ApprovalInitiator approvalInitiator;

    public void generateAndSubmit(Template template, User user, Path destination, Map<String, String> values) throws Exception {

        FilledTemplate filledTemplate =
                documentFiller.fillAndSave(template, user, destination, values);


        documentSigner.prepareAndSign(filledTemplate, user.getId());


        approvalInitiator.initiateApproval(filledTemplate, user.getId());
    }
}
