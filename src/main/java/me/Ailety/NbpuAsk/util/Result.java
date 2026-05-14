package me.Ailety.NbpuAsk.util;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class Result<T> implements Serializable {

    /**
     * 响应状态码
     */
    private Integer code;

    /**
     * 错误信息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 是否响应成功
     */
    private Boolean success;

    // 构造器开始
    /**
     * 无参构造器
     */
    private Result() {
        this.code = 200;
        this.message = ResultCodeEnum.SUCCESS.getMessage();
        this.success = true;
    }

    /**
     * 有参构造器 成功
     * @param obj
     */
    private Result(T obj) {
        this.code = 200;
        this.data = obj;
        this.message = ResultCodeEnum.SUCCESS.getMessage();
        this.success = true;
    }

    /**
     * 有参构造器 无data message取ResultCode
     * @param resultCode
     */
    private Result(ResultCodeEnum resultCode, boolean success) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.success = success;
    }

    /**
     * 有参构造器 无data
     * @param resultCode
     * @param message
     */
    private Result(ResultCodeEnum resultCode, String message, boolean success) {
        this.code = resultCode.getCode();
        this.message = message;
        this.success = success;
    }

    /**
     * 有参构造器 完整 message取ResultCode
     * @param resultCode
     * @param obj
     */
    private Result(ResultCodeEnum resultCode, T obj, boolean success) {
        this.code = resultCode.getCode();
        this.data = obj;
        this.message = resultCode.getMessage();
        this.success = success;
    }

    /**
     * 通用返回成功（没有返回结果）
     * @param <T>
     * @return
     */
    public static<T> Result<T> success(){
        return new Result<>();
    }

    /**
     * 返回成功（有返回结果）
     * @param data
     * @param <T>
     * @return
     */
    public static<T> Result<T> success(T data){
        return new Result<>(data);
    }

    /**
     * 返回成功（有返回结果，且附带状态码）
     * @param resultCode
     * @param data
     * @param <T>
     * @return
     */
    public static<T> Result<T> success(ResultCodeEnum resultCode, T data){
        return new Result<>(resultCode, data, true);
    }

    /**
     * 通用返回失败（没有返回结果）
     * @param resultCode
     * @param <T>
     * @return
     */
    public static<T> Result<T> failure(ResultCodeEnum resultCode){
        return new Result<>(resultCode, false);
    }

    /**
     * 通用返回失败（有返回结果）
     * @param resultCode
     * @param message
     * @param <T>
     * @return
     */
    public static<T> Result<T> failure(ResultCodeEnum resultCode, String message){
        return  new Result<T>(resultCode, message, false);
    }

    @Override
    public String toString() {
        return "Result{" +
                "success=" + success +
                ", code=" + code +
                ", data=" + data +
                ", message='" + message + '\'' +
                '}';
    }
}
