package com.example.ocr;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ExtractedField {
    String label;
    String value;
    Float confidence;
}
