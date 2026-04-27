package com.simplecar.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer code;
    private String msg;
    private T data;

    public static <T> ApiResponse<T> success() {
        return ApiResponse.<T>builder().code(200).msg("操作成功").build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder().code(200).msg("操作成功").data(data).build();
    }

    public static <T> ApiResponse<T> success(String msg, T data) {
        return ApiResponse.<T>builder().code(200).msg(msg).data(data).build();
    }

    public static <T> ApiResponse<T> error(String msg) {
        return ApiResponse.<T>builder().code(500).msg(msg).build();
    }

    public static <T> ApiResponse<T> error(Integer code, String msg) {
        return ApiResponse.<T>builder().code(code).msg(msg).build();
    }
}
