package me.Ailety.NbpuAsk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import me.Ailety.NbpuAsk.model.DTO.LoginResult;
import me.Ailety.NbpuAsk.service.AuthService;
import me.Ailety.NbpuAsk.service.UserService;

import me.Ailety.NbpuAsk.model.User;
import me.Ailety.NbpuAsk.util.JWTUtil;
import me.Ailety.NbpuAsk.util.Result;
import me.Ailety.NbpuAsk.util.ResultCodeEnum;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    AuthService authService;

    @PostMapping(value = "/register")
    public Result<?> register(@RequestBody User user) {

        if (userService.registerService(user) != null) {
            return Result.success();
        } else {
            return Result.failure(ResultCodeEnum.USER_IS_EXITS);
        }

    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody User user) {

        User userLoginRes = userService.loginService(user.getUsername(), user.getPassword());

        if (userLoginRes == null) {
            return Result.failure(ResultCodeEnum.LOGIN_FAILURE);
        } else {
            String token = jwtUtil.generateToken(userLoginRes.getUsername());
            LoginResult loginResult = new LoginResult(
                    userLoginRes.getUserId().toString(),
                    userLoginRes.getUsername(),
                    userLoginRes.getUserData(),
                    token
            );
            return Result.success(loginResult);
        }

    }

    @PostMapping(value = "/get-data")
    public Result<?> getUserData(@RequestHeader("Authorization") String authHeader) {
        User authenticatedUser = authService.getAuthenticatedUser(authHeader);
        if (authenticatedUser == null)
            return Result.failure(ResultCodeEnum.TOKEN_VERIFY_FAIL);

        User existingUser = userService.getUserService(authenticatedUser.getUserId());
        if (existingUser != null) return Result.success(existingUser.getUserData());

        return Result.failure(ResultCodeEnum.FAIL);
    }

    @PostMapping(value = "/set-data")
    public Result<?> setUserData(@RequestHeader("Authorization") String authHeader,
                                 @RequestBody User user) {
        User authenticatedUser = authService.getAuthenticatedUser(authHeader);
        if (authenticatedUser == null)
            return Result.failure(ResultCodeEnum.TOKEN_VERIFY_FAIL);

        user.setUserId(authenticatedUser.getUserId());
        user.setUsername(authenticatedUser.getUsername());
        userService.setUserDataService(user);
        return Result.success(user.getUserData());
    }
}
