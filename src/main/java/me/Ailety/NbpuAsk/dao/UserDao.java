package me.Ailety.NbpuAsk.dao;

import org.apache.ibatis.annotations.Mapper;
import me.Ailety.NbpuAsk.model.User;

@Mapper
public interface UserDao {

    // 注册 返回用户完整数据
    void register(User user);

    // 用户名搜索 返回用户完整数据
    User findByUsername(String username);

    // 用户ID搜索 返回用户完整数据
    User findByUserId(Long userId);

    // 登录 返回用户完整数据
    User login(String username, String password);

    // 更新用户资料
    void setUserData(User user);

}
