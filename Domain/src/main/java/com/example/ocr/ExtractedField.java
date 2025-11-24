package com.example.ocr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@AllArgsConstructor
@ToString
public class ExtractedField {
    String label;
    String value;
    Float confidence;
}
