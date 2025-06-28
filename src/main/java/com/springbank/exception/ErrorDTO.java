
package com.springbank.exception;


class ErrorDTO {
    private final String code;
    private final String message;
    private final String path;
    private final int httpStatus;

    public ErrorDTO(String code, String message, String path, int httpStatus) {
        this.code = code;
        this.message = message;
        this.path = path;
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }
}
