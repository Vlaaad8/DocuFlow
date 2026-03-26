export interface ExtractedField {
    label: string;
    value: string;
    confidence: number;
    sourceOfData: string;
}

export interface UserStoredValue{
  id: number;
  value: string;
  source: string;
  fieldName: string;
  userID: number;
}
