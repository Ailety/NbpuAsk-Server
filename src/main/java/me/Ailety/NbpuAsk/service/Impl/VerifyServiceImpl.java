package me.Ailety.NbpuAsk.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import me.Ailety.NbpuAsk.model.DTO.TokenValidationResult;
import me.Ailety.NbpuAsk.model.User;
import me.Ailety.NbpuAsk.service.VerifyService;
import me.Ailety.NbpuAsk.util.JWTUtil;
import me.Ailety.NbpuAsk.util.Result;
import me.Ailety.NbpuAsk.util.ResultCodeEnum;
import me.Ailety.NbpuAsk.util.StringUtil;

@Service
public class VerifyServiceImpl implements VerifyService {

    @Autowired
    JWTUtil jwtUtil;

    @Override
    public Result<?> verifyRegService(String username, String password) {

        if (!StringUtil.isAlphaNumeric(username) ||
                !StringUtil.isEnglishFirstChar(username) || !StringUtil.hasValidLength(username, 2, 12))
            return Result.failure(ResultCodeEnum.USERNAME_INVALID);

        if (!StringUtil.isValidPassword(password)) return Result.failure(ResultCodeEnum.PASSWORD_INVALID);

        return Result.success(ResultCodeEnum.SUCCESS);
    }

    @Override
    public Result<?> verifyAuthTokenService(String token, User user) {

        TokenValidationResult result = jwtUtil.verifyToken(token, user);

        return buildTokenVerifyResult(result);

    }

    @Override
    public Result<?> verifyAuthTokenService(String token) {

        TokenValidationResult result = jwtUtil.verifyToken(token);

        return buildTokenVerifyResult(result);

    }

    private Result<?> buildTokenVerifyResult(TokenValidationResult result) {

        // 过期
        if (result.getCode() == 401)
            return Result.success(ResultCodeEnum.TOKEN_EXPIRED, result.getNewToken());

        // 无效
        if (result.getCode() == 402)
            return Result.failure(ResultCodeEnum.TOKEN_INVALID);

        // 不匹配
        if (result.getCode() == 403)
            return Result.failure(ResultCodeEnum.TOKEN_MISMATCHING);

        // 通过
        return Result.success();

    }

}
