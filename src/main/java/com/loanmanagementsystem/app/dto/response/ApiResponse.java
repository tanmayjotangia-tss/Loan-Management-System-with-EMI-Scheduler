package com.loanmanagementsystem.app.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    private int status;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(int status, String message, T data) {
        return ApiResponse.<T>builder()
                .status(status)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(200, "Success", data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return success(201, "Resource created successfully", data);
    }

    public static <T> ApiResponse<T> noContent() {
        return ApiResponse.<T>builder()
                .status(204)
                .message("No content")
                .data(null)
                .build();
    }

    public static <T> ApiResponse<T> error(int status, String message) {
        return ApiResponse.<T>builder()
                .status(status)
                .message(message)
                .data(null)
                .build();
    }
}