package me.Ailety.NbpuAsk.handler;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import me.Ailety.NbpuAsk.util.Result;
import me.Ailety.NbpuAsk.util.ResultCodeEnum;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 请求头为空
    @ExceptionHandler(MissingRequestHeaderException.class)
    public Result<?> handleMissingHeader(MissingRequestHeaderException ex) {
        String headerName = ex.getHeaderName();
        if (headerName.equals("Authorization")) {
            return Result.failure(ResultCodeEnum.UNAUTHORIZED);
        }
        return Result.failure(ResultCodeEnum.PARAMS_IS_BLANK);
    }

    // 请求体为空
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<?> handleMessageNotReadable() {
        return Result.failure(ResultCodeEnum.PARAMS_IS_BLANK);
    }

//    // 数据库请求失败
//    @ExceptionHandler(ConnectException.class)
//    public Result<?> handleConnectError() {
//        return Result.failure(ResultCodeEnum.INTERNAL_SERVER_ERROR);
//    }
//
//    // 代码执行错误
//    @ExceptionHandler(NullPointerException.class)
//    public Result<?> handleNullPointer() {
//        return Result.failure(ResultCodeEnum.INTERNAL_SERVER_ERROR);
//    }

    // 接口方法调用错误
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<?> handleMethodNotSupported() {
        return Result.failure(ResultCodeEnum.METHOD_NOT_ALLOWED);
    }

}
