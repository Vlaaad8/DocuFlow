package com.example.ocr;

import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClient;
import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClientBuilder;
import com.azure.ai.formrecognizer.documentanalysis.models.AnalyzeResult;
import com.azure.ai.formrecognizer.documentanalysis.models.OperationResult;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.BinaryData;
import com.azure.core.util.polling.SyncPoller;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class AzureDocumentAdapter implements DocumentPort {
    private final DocumentAnalysisClient documentClient;

    public AzureDocumentAdapter(AzureProperties azureProperties) {
        this.documentClient = new DocumentAnalysisClientBuilder().credential(new AzureKeyCredential(azureProperties.getKey())).endpoint(azureProperties.getEndpoint()).buildClient();
    }

    @Override
    public List<ExtractedField> extractText(InputStream fileStream, long fileLength) {

        BinaryData fileData = BinaryData.fromStream(fileStream, fileLength);

        SyncPoller<OperationResult, AnalyzeResult> poller =
                documentClient.beginAnalyzeDocument("prebuilt-document", fileData);
        AnalyzeResult result = poller.getFinalResult();

        List<ExtractedField> allExtractedFields = new ArrayList<>();

        String rawContent = result.getContent();
        if (rawContent == null || rawContent.isEmpty()) {
            return allExtractedFields;
        }

        if (result.getKeyValuePairs() != null && !result.getKeyValuePairs().isEmpty()) {

            result.getKeyValuePairs().forEach(kvp -> {
                String key = (kvp.getKey() != null) ? kvp.getKey().getContent() : "KeyNull";
                String value = (kvp.getValue() != null) ? kvp.getValue().getContent() : "ValueNull";

                allExtractedFields.add(new ExtractedField(key, value, kvp.getConfidence()));
            });
        }

        if (allExtractedFields.isEmpty() && result.getParagraphs() != null) {
            result.getParagraphs().forEach(paragraph -> {
                allExtractedFields.add(new ExtractedField("CONTENT_BLOCK", paragraph.getContent(), 1.0f));
            });
        }
        return allExtractedFields;
    }
}