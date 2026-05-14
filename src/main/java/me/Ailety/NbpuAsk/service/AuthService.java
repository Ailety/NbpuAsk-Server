package me.Ailety.NbpuAsk.service;

import me.Ailety.NbpuAsk.model.User;

public interface AuthService {

    User getAuthenticatedUser(String authHeader);

    User getAuthenticatedUser(String authHeader, Long expectedUserId);
}
