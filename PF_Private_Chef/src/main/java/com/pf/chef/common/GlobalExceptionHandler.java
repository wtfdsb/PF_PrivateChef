package com.pf.chef.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理 —— 保证任何异常都以统一结构返回，前端不用做兼容分支。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务异常：文案直接给用户看 */
    @ExceptionHandler(BizException.class)
    public R<Void> handleBiz(BizException e) {
        log.warn("业务异常: {}", e.getMessage());
        return R.fail(e.getCode(), e.getMessage());
    }

    /** @Valid 参数校验失败：取第一条提示 */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public R<Void> handleValid(BindException e) {
        FieldError fe = e.getBindingResult().getFieldError();
        String msg = fe != null ? fe.getDefaultMessage() : "参数不合法";
        return R.fail(400, msg);
    }

    /** 兜底：不把堆栈暴露给前端，但日志要留全。
     *  带异常类型是为了远程排障（私用系统，不含敏感信息） */
    @ExceptionHandler(Exception.class)
    public R<Void> handleOther(Exception e) {
        log.error("系统异常", e);
        String detail = e.getClass().getSimpleName()
                + (e.getMessage() != null && !e.getMessage().isBlank() ? ": " + e.getMessage() : "");
        // 截断，防止长文本刷屏
        if (detail.length() > 160) {
            detail = detail.substring(0, 160) + "...";
        }
        return R.fail(500, "服务开小差了（" + detail + "）");
    }
}
