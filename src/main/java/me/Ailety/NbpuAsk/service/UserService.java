package me.Ailety.NbpuAsk.service;

import org.springframework.stereotype.Service;
import me.Ailety.NbpuAsk.model.User;

@Service
public interface UserService {

    User registerService(User user);

    User loginService(String username, String password);

    User getUserService(Long userId);

    void setUserDataService(User user);

}
