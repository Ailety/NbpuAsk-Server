package me.Ailety.NbpuAsk.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StringUtil {

    // 判断字符串是否为空，为空则处理
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty() || str.trim().isEmpty();
    }

    // 判断字符串是否只包括数字和字母
    public static boolean isAlphaNumeric(String str) {
        if (isEmpty(str)) return false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!Character.isDigit(c) && !(c >= 'A' && c <= 'Z') && !(c >= 'a' && c <= 'z')) {
                return false;
            }
        }
        return true;
    }

    // 判断第一个字符串是否是英文字母
    public static boolean isEnglishFirstChar(String str) {
        if (isEmpty(str)) return false;
        char firstChar = str.charAt(0);
        return (firstChar >= 65 && firstChar <= 90) || (firstChar >= 97 && firstChar <= 122);
    }

    // 判断字符串长度是否在范围内
    public static boolean hasValidLength(String str, int min, int max) {
        if (str == null) return false;
        int length = str.length();
        return length >= min && length <= max;
    }

    // 判断密码是否有效
    public static boolean isValidPassword(String password) {
        // 正则表达式分解说明：
        // ^                 - 字符串开始
        // (?=.*\\d)         - 至少一个数字（正向预查）
        // (?=.*[A-Za-z])    - 至少一个字母（不区分大小写）
        // [!-~]{8,16}       - 允许ASCII码33-126的字符（即所有可打印非空字符），长度8-16
        // $                 - 字符串结束
        String regex = "^(?=.*\\d)(?=.*[A-Za-z])[!-~]{8,16}$";
        return password != null && password.matches(regex);
    }

    // 将字符串处理为Map(伪JSON)
    public static Map<String, String> toJsonMap(String json) {

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> jsonMap = new HashMap<>();

        try {
            jsonMap = objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonMap;

    }

}
