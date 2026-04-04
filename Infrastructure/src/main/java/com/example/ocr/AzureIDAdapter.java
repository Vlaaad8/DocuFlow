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
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

        String expirationDateRaw = expElement != null ? expElement.value() : null;
        String issueDateRaw = issueElement != null ? issueElement.value() : null;


        if (expirationDateRaw != null && expirationDateRaw.equals(issueDateRaw) && expirationDateRaw.contains("-")) {
            String[] dateParts = expirationDateRaw.split("-");
            if (dateParts.length == 2) {
                String standardizedIssue = standardizeDate(dateParts[0].trim());
                String standardizedExp = standardizeDate(dateParts[1].trim());

                extractedData.put("DateOfIssue", new AzureElement(standardizedIssue, issueElement.confidence(), issueElement.documentType()));
                extractedData.put("DateOfExpiration", new AzureElement(standardizedExp, expElement.confidence(), expElement.documentType()));
                return;
            }
        }


        String stdIssue = issueDateRaw != null ? standardizeDate(issueDateRaw) : null;
        String stdExp = expirationDateRaw != null ? standardizeDate(expirationDateRaw) : null;

        if (stdIssue != null && stdExp != null) {

            if (stdExp.compareTo(stdIssue) < 0) {
                extractedData.put("DateOfExpiration", new AzureElement(stdIssue, expElement.confidence(), expElement.documentType()));
                extractedData.put("DateOfIssue", new AzureElement(stdExp, issueElement.confidence(), issueElement.documentType()));
            } else {

                extractedData.put("DateOfExpiration", new AzureElement(stdExp, expElement.confidence(), expElement.documentType()));
                extractedData.put("DateOfIssue", new AzureElement(stdIssue, issueElement.confidence(), issueElement.documentType()));
            }
        } else {

            if (stdIssue != null) {
                extractedData.put("DateOfIssue", new AzureElement(stdIssue, issueElement.confidence(), issueElement.documentType()));
            }
            if (stdExp != null) {
                extractedData.put("DateOfExpiration", new AzureElement(stdExp, expElement.confidence(), expElement.documentType()));
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
    private String standardizeDate(String rawDate) {
        if (rawDate == null || rawDate.isBlank()) return rawDate;


        String cleaned = rawDate.trim().replaceAll("\\s+", " ");


        String[] parts = cleaned.split("[\\s./\\-]+");

        if (parts.length != 3) {
            return cleaned;
        }

        try {
            int a = Integer.parseInt(parts[0]);
            int b = Integer.parseInt(parts[1]);
            int c = Integer.parseInt(parts[2]);

            int day, month, year;


            if (c > 31) {

                year = c;
                if (a > 12) {

                    day = a;
                    month = b;
                } else if (b > 12) {

                    day = b;
                    month = a;
                } else {

                    day = a;
                    month = b;
                }
            } else if (a > 31) {

                year = a;
                month = b;
                day = c;
            } else {

                if (a > 12 || (a <= 12 && b > 12)) {
                    day = a;
                    month = b;
                    year = c;
                } else {

                    day = a;
                    month = b;
                    year = c;
                }
            }


            if (year >= 0 && year <= 99) {
                year += 2000;
            }


            LocalDate date = LocalDate.of(year, month, day);
            return date.toString();

        } catch (NumberFormatException | DateTimeException e) {

            return cleaned;
        }
    }

}
