package me.Ailety.NbpuAsk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import me.Ailety.NbpuAsk.model.User;
import me.Ailety.NbpuAsk.service.VerifyService;
import me.Ailety.NbpuAsk.util.Result;
import me.Ailety.NbpuAsk.util.ResultCodeEnum;

@RestController
@RequestMapping("/verify")
public class VerifyController {

    @Autowired
    VerifyService verifyService;

    // 判断用户提交的注册数据是否合法
    @PostMapping(value = "/register")
    public Result<?> verifyRegister(@RequestBody User user) {

        return verifyService.verifyRegService(user.getUsername(), user.getPassword());

    }

    // 判断用户浏览器本地存储的auth_token的状态是否正常
    @PostMapping(value = "/auth-token")
    public Result<?> verifyToken(@RequestHeader("Authorization") String authHeader) {

        if (authHeader.length() <= 7 || !authHeader.startsWith("Bearer ")) {
            return Result.failure(ResultCodeEnum.PARAMS_IS_INVALID);
        }
        String authToken = authHeader.substring(7);
        return verifyService.verifyAuthTokenService(authToken);

    }


}
