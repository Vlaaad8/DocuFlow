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
import java.util.HashMap;
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

            Map<String, AzureElement> extractedData = new HashMap<>();

            AnalyzeDocumentOptions options =
                    new AnalyzeDocumentOptions(BinaryData.fromBytes(fileBytes));

            List<String> query = List.of("IssuedBy");

            options.setQueryFields(query);
            options.setDocumentAnalysisFeatures(List.of(DocumentAnalysisFeature.QUERY_FIELDS));
            SyncPoller<AnalyzeOperationDetails, AnalyzeResult> poller =
                    documentClient.beginAnalyzeDocument("prebuilt-idDocument", options);

            AnalyzeResult result = poller.getFinalResult();


            if (result.getDocuments() == null || result.getDocuments().isEmpty()) {
                return List.of();
            }

            for (AnalyzedDocument doc : result.getDocuments()) {
                ;
                Map<String, DocumentField> fields = doc.getFields();

                //TODO DocumentNumber is not processed ok
                fields.forEach((key, field) -> {
                    String value = null;
                    if (field.getType() == STRING) {
                        value = field.getValueString();
                    } else if (field.getType() == COUNTRY_REGION) {
                        value = field.getValueCountryRegion();
                    } else if (field.getType() == DATE) {
                        value = field.getContent();
                    } else if (field.getType() == ADDRESS) {
                        value = field.getContent();
                    }
                    //TODO MachineReadableZone - very important , can double check data and provides me with relevant information
                    if (value != null) {
                        float confidence = (float) (field.getConfidence() * 100);
                        extractedData.put(key, new AzureElement(value, confidence, doc.getDocumentType()));
                    }
                });
            }
            validateExtractedData(extractedData);

            return convertMapToList(extractedData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SourceOfData mapSourceOfData(String source) {
        return switch (source) {
            case ("idDocument.nationalId") -> SourceOfData.NATIONAL_IDENTITY_CARD;
            case ("idDocument.passport") -> SourceOfData.PASSPORT;
            case ("idDocument.driverLicense") -> SourceOfData.DRIVER_LICENSE;
            case ("idDocument.residencePermit") -> SourceOfData.RESIDENCE_PERMIT;
            case ("idDocument.socialSecurityCard") -> SourceOfData.SOCIAL_SECURITY_CARD;
            default -> SourceOfData.UNKNOWN;
        };
    }

    private void validateExtractedData(Map<String, AzureElement> extractedData) {
        validateDocumentNumber(extractedData);
        validateExpirationIssueDate(extractedData);
        validateIssueDate(extractedData);
    }

    private void validateExpirationIssueDate(Map<String, AzureElement> extractedData) {
        AzureElement expElement = extractedData.get("DateOfExpiration");
        AzureElement issueElement = extractedData.get("DateOfIssue");


        if (expElement != null && issueElement != null) {
            String expirationDate = expElement.value();
            String issueDate = issueElement.value();


            if (expirationDate != null && expirationDate.equals(issueDate)) {

                if (expirationDate.contains("-")) {

                    String[] dateParts = expirationDate.split("-");


                    extractedData.put("DateOfExpiration", new AzureElement(
                            dateParts[1],
                            expElement.confidence(),
                            expElement.documentType()
                    ));

                    extractedData.put("DateOfIssue", new AzureElement(
                            dateParts[0],
                            expElement.confidence(),
                            expElement.documentType()
                    ));

                }
            }
            if (expirationDate != null && issueDate != null) {
                if (expirationDate.compareTo(issueDate) < 0) {
                    extractedData.put("DateOfExpiration", new AzureElement(
                            issueDate,
                            expElement.confidence(),
                            expElement.documentType()
                    ));

                    extractedData.put("DateOfIssue", new AzureElement(
                            expirationDate,
                            expElement.confidence(),
                            expElement.documentType()
                    ));
                }
            }
        }
    }


    private void validateDocumentNumber(Map<String, AzureElement> extractedData) {
        AzureElement docNumberElement = extractedData.get("DocumentNumber");
        if (docNumberElement != null) {
            String docNumber = docNumberElement.value();
            if (docNumber != null && docNumber.contains(" ")) {
                String cleanedDocNumber = docNumber.replaceAll("\\s+", "");
                extractedData.put("DocumentNumber", new AzureElement(
                        cleanedDocNumber,
                        docNumberElement.confidence(),
                        docNumberElement.documentType()
                ));
            }
        }
    }

    private List<ExtractedField> convertMapToList(Map<String, AzureElement> extractedData) {
        List<ExtractedField> extractedFields = new ArrayList<>();
        extractedData.forEach((key, element) -> {
            ExtractedField field = new ExtractedField();
            field.setLabel(key);
            field.setValue(element.value());
            field.setConfidence(element.confidence());
            field.setSourceOfData(element.documentType);
            extractedFields.add(field);
        });
        return extractedFields;
    }

    private void validateIssueDate(Map<String, AzureElement> extractedData) {
        AzureElement issueDateElement = extractedData.get("DateOfIssue");
        if (issueDateElement != null) {
            String[] dateParts = issueDateElement.value().split("\\.");
            if (dateParts.length == 3 && dateParts[2].length() == 2) {
                String reformattedDate = dateParts[0] + "." + dateParts[1] + "." + "20" + dateParts[2];
                extractedData.put("DateOfIssue", new AzureElement(
                        reformattedDate,
                        issueDateElement.confidence(),
                        issueDateElement.documentType()
                ));
            }


        }
    }
    private void validateIssuedBy(Map<String, AzureElement> extractedData) {
        AzureElement issuedByElement = extractedData.get("IssuedBy");
        if (issuedByElement != null) {
            String issuedBy = issuedByElement.value();
            if (issuedBy != null) {
                String[] parts = issuedBy.split(",");
                if (parts.length > 0) {
                    String country = parts[parts.length - 1].trim();
                    extractedData.put("IssuedBy", new AzureElement(
                            country,
                            issuedByElement.confidence(),
                            issuedByElement.documentType()
                    ));
                }
            }
        }
    }

    private record AzureElement(String value, float confidence, String documentType) {

    }
}
