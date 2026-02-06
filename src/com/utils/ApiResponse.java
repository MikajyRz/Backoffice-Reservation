package com.utils;

import java.lang.reflect.Array;
import java.util.Collection;

public class ApiResponse {
    private String status;
    private int code;
    private int count;
    private Object data;

    public ApiResponse(String status, int code, int count, Object data) {
        this.status = status;
        this.code = code;
        this.count = count;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public int getCode() {
        return code;
    }

    public int getCount() {
        return count;
    }

    public Object getData() {
        return data;
    }

    public static ApiResponse from(Object result) {
        if (result == null) {
            return new ApiResponse("success", 200, 0, null);
        }

        if (result instanceof com.classes.ModelView mv) {
            int count = mv.getData() != null ? mv.getData().size() : 0;
            return new ApiResponse("success", 200, count, mv.getData());
        }

        if (result instanceof Collection<?> col) {
            return new ApiResponse("success", 200, col.size(), col);
        }

        if (result.getClass().isArray()) {
            int len = Array.getLength(result);
            return new ApiResponse("success", 200, len, result);
        }

        return new ApiResponse("success", 200, 1, result);
    }
}
