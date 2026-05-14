package me.Ailety.NbpuAsk.service;

import org.springframework.stereotype.Service;
import me.Ailety.NbpuAsk.model.User;
import me.Ailety.NbpuAsk.util.Result;

@Service
public interface VerifyService {

    Result<?> verifyRegService(String username, String password);

    Result<?> verifyAuthTokenService(String token, User user);

    Result<?> verifyAuthTokenService(String token);

}
