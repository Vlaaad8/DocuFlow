package com.example.state;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class RequestPipeline {
    private final ValidationStep validationStep;
    private final AnswerStep answerStep;
    private final SignatureStep signatureStep;
    private final AdvanceChainStep advanceChainStep;

    public void execute(RequestContext context) {
        List<RequestStep> steps = List.of(
                validationStep,
                answerStep,
                signatureStep,
                advanceChainStep
        );

        run(steps, 0, context);
    }

    private void run(List<RequestStep> steps, int index, RequestContext context) {
        if (index < steps.size()) {
            steps.get(index).execute(context, () -> run(steps, index + 1, context));
        }
    }
}
