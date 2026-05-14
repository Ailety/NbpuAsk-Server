package me.Ailety.NbpuAsk.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import me.Ailety.NbpuAsk.dao.UserDao;
import me.Ailety.NbpuAsk.model.User;
import me.Ailety.NbpuAsk.service.UserService;
import me.Ailety.NbpuAsk.util.encrypt.BCryptUtil;

@Service
public class UserServiceImpl implements UserService {

    @Autowired(required = false)
    UserDao userDao;

    @Override
    public User registerService(User user){

        User temp = userDao.findByUsername(user.getUsername());
        if (temp != null) {
            return null;
        } else {
            // 注册操作，对密码进行哈希加密
            user.setPassword(BCryptUtil.hashPassword(user.getPassword()));
            userDao.register(user);
            return user;
        }

    }

    @Override
    public User loginService(String username, String password) {

        User temp = userDao.findByUsername(username);
        if (temp == null) return null;
        boolean isVerify = BCryptUtil.checkPassword(password, temp.getPassword());
        if (isVerify) {
            return userDao.login(username, temp.getPassword());
        } else {
            return null;
        }

    }

    @Override
    public User getUserService(Long userId) {
        return userDao.findByUserId(userId);
    }

    @Override
    public void setUserDataService(User user) {
        userDao.setUserData(user);
    }

}
