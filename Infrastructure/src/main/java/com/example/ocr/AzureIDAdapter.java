package com.example.ocr;

import com.azure.ai.documentintelligence.DocumentIntelligenceClient;
import com.azure.ai.documentintelligence.DocumentIntelligenceClientBuilder;
import com.azure.ai.documentintelligence.models.*;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.BinaryData;
import com.azure.core.util.polling.SyncPoller;
import com.example.template.SourceOfData;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.azure.ai.documentintelligence.models.DocumentFieldType.*;


@Component
public class AzureIDAdapter implements IdPort {
    private final DocumentIntelligenceClient documentClient;


    public AzureIDAdapter(AzureProperties azureProperties) {
        this.documentClient = new DocumentIntelligenceClientBuilder().credential(new AzureKeyCredential(azureProperties.getKey())).endpoint(azureProperties.getEndpoint()).buildClient();
    }

    @Override
    public List<ExtractedField> analyzeId(InputStream fileStream, long fileLength) {
        try {
            byte[] fileBytes = fileStream.readAllBytes();


            AnalyzeDocumentOptions options =
                    new AnalyzeDocumentOptions(BinaryData.fromBytes(fileBytes));


            SyncPoller<AnalyzeOperationDetails, AnalyzeResult> poller =
                    documentClient.beginAnalyzeDocument("prebuilt-idDocument", options);

            AnalyzeResult result = poller.getFinalResult();

            List<ExtractedField> allExtractedFields = new ArrayList<>();

            if (result.getDocuments() == null || result.getDocuments().isEmpty()) {
                return allExtractedFields;

            }
            for (AnalyzedDocument doc : result.getDocuments()) {;
                Map<String, DocumentField> fields = doc.getFields();
                //TODO DocumentNumber is not processed ok
                fields.forEach((key, field) -> {
                    String value = null;
                    if (field.getType() == STRING) {
                        value = field.getValueString();
                    } else if (field.getType() == COUNTRY_REGION) {
                        value = field.getValueCountryRegion();
                    } else if (field.getType() == DATE) {
                        value = field.getValueDate().toString();
                    } else if (field.getType() == ADDRESS) {
                        //TODO split into sections and to make it work as a whole - temp disabled
                        AddressValue address = field.getValueAddress();
                        if(address.getCity() != null){

                        }
                    }
                    //TODO MachineReadableZone - very important , can double check data and provides me with relevant information
                    if (value != null) {
                        Float confidence = (float) (field.getConfidence() * 100);
                        allExtractedFields.add(new ExtractedField(key, value, confidence,doc.getDocumentType()));
                    }
                });
            }

            return allExtractedFields;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private SourceOfData mapSourceOfData(String source) {
        switch (source) {
            case ("idDocument.nationalId"):
                return SourceOfData.NATIONAL_IDENTITY_CARD;
            case ("idDocument.passport"):
                return SourceOfData.PASSPORT;
            case ("idDocument.driverLicense"):
                return SourceOfData.DRIVER_LICENSE;
            case ("idDocument.residencePermit"):
                return SourceOfData.RESIDENCE_PERMIT;
            case ("idDocument.socialSecurityCard"):
                return SourceOfData.SOCIAL_SECURITY_CARD;
            default:
                return SourceOfData.UNKNOWN;
        }
    }
}
