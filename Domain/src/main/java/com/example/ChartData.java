package com.example;

import java.sql.Timestamp;

public record ChartData(String title, Timestamp timestamp, String status) {
}

