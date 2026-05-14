package me.Ailety.NbpuAsk.service.Impl;

import me.Ailety.NbpuAsk.dao.UserDao;
import me.Ailety.NbpuAsk.model.DTO.TokenValidationResult;
import me.Ailety.NbpuAsk.model.User;
import me.Ailety.NbpuAsk.service.AuthService;
import me.Ailety.NbpuAsk.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserDao userDao;

    @Override
    public User getAuthenticatedUser(String authHeader) {
        String token = extractBearerToken(authHeader);
        if (token == null) {
            return null;
        }

        TokenValidationResult result = jwtUtil.verifyToken(token);
        if (result.getCode() != 200 && result.getCode() != 401) {
            return null;
        }

        String username = result.getUsername();
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        return userDao.findByUsername(username);
    }

    @Override
    public User getAuthenticatedUser(String authHeader, Long expectedUserId) {
        User authenticatedUser = getAuthenticatedUser(authHeader);
        if (authenticatedUser == null || expectedUserId == null) {
            return null;
        }

        if (!expectedUserId.equals(authenticatedUser.getUserId())) {
            return null;
        }

        return authenticatedUser;
    }

    private String extractBearerToken(String authHeader) {
        if (authHeader == null || authHeader.length() <= 7 || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        return authHeader.substring(7);
    }
}
