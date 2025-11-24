package com.example.ocr;

import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClient;
import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClientBuilder;
import com.azure.ai.formrecognizer.documentanalysis.models.*;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.BinaryData;
import com.azure.core.util.polling.SyncPoller;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
public class AzureIDAdapter implements IdPort {
    private final DocumentAnalysisClient documentClient;


    public AzureIDAdapter(AzureProperties azureProperties) {
        this.documentClient = new DocumentAnalysisClientBuilder().credential(new AzureKeyCredential(azureProperties.getKey())).endpoint(azureProperties.getEndpoint()).buildClient();
    }

    @Override
    public List<ExtractedField> analyzeId(InputStream fileStream, long fileLength) {
        BinaryData fileData = BinaryData.fromStream(fileStream, fileLength);

        SyncPoller<OperationResult, AnalyzeResult> poller =
                documentClient.beginAnalyzeDocument("prebuilt-idDocument", fileData);

        AnalyzeResult result = poller.getFinalResult();

        List<ExtractedField> allExtractedFields = new ArrayList<>();

        if (result.getDocuments() == null || result.getDocuments().isEmpty()) {
            return allExtractedFields;
        }

        for (AnalyzedDocument doc : result.getDocuments()) {

            Map<String, DocumentField> fields = doc.getFields();

            fields.forEach((key, field) -> {
                String value = field.getContent();
                Float confidence = field.getConfidence()*100;

                allExtractedFields.add(new ExtractedField(key, value, confidence));
            });
        }

        return allExtractedFields;
    }

}
