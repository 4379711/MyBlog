package com.my.blog.website.constant;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liuyalong
 */
@Component
public class WebConst {

    /**
     * session cookie 过期时间 单位:秒
     */
    public static final int SESSION_TIMEOUT = 60 * 60 * 10;
    public static final int COOKIE_TIMEOUT = 60 * 60 * 10;
    public static final int CSRF_TOKEN_TIMEOUT = 60 * 60 * 10;

    /**
     * 密码错误超过最大值等待时间
     */
    public static final int ERROR_PASSWORD_TIMEOUT = 60 * 30;
    public static final int ERROR_PASSWORD_TIMES = 3;


    public static Map<String, String> initConfig = new HashMap<>();


    public static final String LOGIN_SESSION_KEY = "login_user";

    public static final String USER_IN_COOKIE = "S_L_ID";

    /**
     * aes加密加盐
     */
    public static String AES_SALT = "0a1234g56789acef";

    /**
     * 最大获取文章条数
     */
    public static final int MAX_POSTS = 9999;

    /**
     * 最大页码
     */
    public static final int MAX_PAGE = 100;

    /**
     * 文章最多可以输入的文字数
     */
    public static final int MAX_TEXT_COUNT = 200000;

    /**
     * 文章标题最多可以输入的文字个数
     */
    public static final int MAX_TITLE_COUNT = 200;

    /**
     * 点击次数超过多少更新到数据库
     */
    public static final int HIT_EXCEED = 10;

    /**
     * 上传文件最大10M
     * 注意不能比配置文件里的spring.servlet.multipart.max-file-size值大
     */
    public static Integer MAX_FILE_SIZE = 1024 * 1024 * 10;
}
