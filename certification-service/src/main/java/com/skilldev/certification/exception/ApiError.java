package com.skilldev.certification.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public record ApiError(
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    String timestamp,
    int status,
    String error
) {
    public static ApiError of(int status, String message) {
        return new ApiError(
            OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            status,
            message != null ? message : ""
        );
    }
}
