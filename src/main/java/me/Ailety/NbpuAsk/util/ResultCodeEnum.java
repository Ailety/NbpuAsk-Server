package me.Ailety.NbpuAsk.util;

public enum ResultCodeEnum {

    // 通用参数
    SUCCESS(200, "成功"), // 成功
    FAIL(400, "失败"), // 失败
    BAD_REQUEST(400, "请求错误"), // 异常响应
    UNAUTHORIZED(401, "认证失败"), // 请求头不包括Authorization
    NOT_FOUND(404, "接口不存在"), // 接口不存在
    INTERNAL_SERVER_ERROR(500, "系统处理异常"), // 服务器内部错误
    METHOD_NOT_ALLOWED(405,"接口方法错误"), // 调用了错误方法

    /* 自定义参数 1001-1999 */
    PARAMS_IS_INVALID(1001, "参数无效"),
    // 请求头参数缺失(除Authorization外) 请求体参数缺失
    PARAMS_IS_BLANK(1002, "参数为空"),

    /* 用户登录注册、令牌相关参数 1003-1199 */
    USERNAME_INVALID(1003, "用户名不合法"),
    PASSWORD_INVALID(1004, "密码不合法"),
    USER_IS_EXITS(1005, "用户已存在"),
    LOGIN_FAILURE(1006, "用户名或密码错误"),
    TOKEN_EXPIRED(1007, "令牌过期"),
    TOKEN_INVALID(1008, "令牌无效"),
    TOKEN_MISMATCHING(1009, "令牌信息不匹配"),
    TOKEN_VERIFY_FAIL(1010, "令牌校验未通过"),

    /* 对话相关参数 1300-1399 */
    CONV_CREATE_EXCEPTION(1300, "对话新建异常"),
    CONV_GET_EXCEPTION(1301, "对话获取异常"),
    CONV_SET_EXCEPTION(1302, "对话设置异常"),
    CONV_DELETE_EXCEPTION(1303, "对话删除异常"),
    CONV_NOT_FOUND(1304, "对话不存在"),
    CONV_RUN_EXCEPTION(1305, "对话响应异常"),
    CONV_PENDING_RESPONSE(1306, "当前对话仍有未完成的模型响应");

    /**
     * 响应状态码
     */
    private Integer code;

    /**
     * 响应信息
     */
    private String message;

    ResultCodeEnum(Integer code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
