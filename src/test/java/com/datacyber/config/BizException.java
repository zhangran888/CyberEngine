package com.datacyber.config;

import lombok.Getter;


@Getter
public class BizException extends RuntimeException {

    private final String code;
    private final String message;

    public BizException(ResultCode resultCode) {
        super(resultCode.getValue());
        this.code = resultCode.getCode();
        this.message = resultCode.getValue();
    }

    public BizException(String message) {
        super(message);
        this.code = ResultCode.SYSTEM_INNER_ERR.getCode();
        this.message = message;
    }

    public BizException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
