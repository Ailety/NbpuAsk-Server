package me.Ailety.NbpuAsk.model.DTO;

import lombok.Data;
import me.Ailety.NbpuAsk.model.User;

@Data
public class ShareOwnerInfo {

    private Long userId;
    private String username;
    private String nickname;
    private String intro;
    private Object registrationDate;

    public ShareOwnerInfo() {}

    public ShareOwnerInfo(User user) {
        if (user == null) {
            return;
        }

        this.userId = user.getUserId();
        this.username = user.getUsername();

        if (user.getUserData() != null) {
            this.nickname = user.getUserData().getNickname();
            this.registrationDate = user.getUserData().getRegistrationDate();
            if (user.getUserData().getSettings() != null) {
                this.intro = user.getUserData().getSettings().getText();
            }
        }

        if (this.nickname == null || this.nickname.trim().isEmpty()) {
            this.nickname = user.getUsername();
        }
        if (this.intro == null || this.intro.trim().isEmpty()) {
            this.intro = "该用户暂无简介。";
        }
    }
}
