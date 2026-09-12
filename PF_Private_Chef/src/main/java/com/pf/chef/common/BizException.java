package com.pf.chef.common;

/**
 * 业务异常 —— 抛出后由 GlobalExceptionHandler 转成 { code, msg }
 * 这类异常的 msg 会原样回给用户，所以文案要写成人话。
 */
public class BizException extends RuntimeException {

    private final int code;

    public BizException(String msg) {
        this(400, msg);
    }

    public BizException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
