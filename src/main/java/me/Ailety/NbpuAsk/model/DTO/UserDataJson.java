package me.Ailety.NbpuAsk.model.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Data;


@Data
public class UserDataJson {

        private String nickname;
        // 直接指定日期格式与时区
        @JsonFormat(
                shape = JsonFormat.Shape.STRING,
                pattern = "yyyy/M/d",
                timezone = "GMT+8"
        )
        private Date registrationDate;
        private UserSettingsJson settings;

}
