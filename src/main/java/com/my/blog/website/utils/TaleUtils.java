package com.my.blog.website.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.blog.website.constant.WebConst;
import com.my.blog.website.controller.admin.AttachController;
import com.my.blog.website.model.Vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tale工具类
 *
 * @author liuyalong
 */
public class TaleUtils {

    private static final Pattern SLUG_REGEX = Pattern.compile("^[A-Za-z0-9_-]{5,100}$", Pattern.CASE_INSENSITIVE);
    /**
     * markdown解析器
     */
    private static final Parser PARSER = Parser.builder().build();


    /**
     * md5加密
     *
     * @param source 数据源
     * @return 加密字符串
     */
    public static String mD5encode(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ignored) {
        }
        assert messageDigest != null;
        byte[] encode = messageDigest.digest(source.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte anEncode : encode) {
            String hex = Integer.toHexString(0xff & anEncode);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * 返回当前登录用户
     */
    public static UserVo getLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (null == session) {
            // 尝试从cookies中获取用户信息
            String cookieUser = getCookieUid(request);

            if (null != cookieUser) {

                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    return objectMapper.readValue(cookieUser, UserVo.class);
                } catch (JsonProcessingException ignore) {
                }
            }
            return null;
        }
        return (UserVo) session.getAttribute(WebConst.LOGIN_SESSION_KEY);
    }


    /**
     * 获取cookie中的用户id
     */
    public static String getCookieUid(HttpServletRequest request) {
        if (null != request) {
            Cookie cookie = cookieRaw(request);
            if (cookie != null && cookie.getValue() != null) {
                try {
                    return Tools.deAes(cookie.getValue(), WebConst.AES_SALT);
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }

    /**
     * 从cookies中获取指定cookie
     *
     * @param request 请求
     * @return cookie
     */
    private static Cookie cookieRaw(HttpServletRequest request) {
        javax.servlet.http.Cookie[] servletCookies = request.getCookies();
        if (servletCookies == null) {
            return null;
        }
        for (javax.servlet.http.Cookie c : servletCookies) {
            if (c.getName().equals(WebConst.USER_IN_COOKIE)) {
                return c;
            }
        }
        return null;
    }

    /**
     * 设置记住密码cookie
     */
    public static void setCookie(HttpServletResponse response, String user) {
        try {
            String val = Tools.enAes(user, WebConst.AES_SALT);

            Cookie cookie = new Cookie(WebConst.USER_IN_COOKIE, val);
            cookie.setPath("/");
            cookie.setMaxAge(WebConst.COOKIE_TIMEOUT);
            cookie.setSecure(false);
            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 提取html中的文字
     */
    public static String htmlToText(String html) {
        if (StringUtils.isNotBlank(html)) {
            return html.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");
        }
        return "";
    }

    /**
     * markdown转换为html
     */
    public static String mdToHtml(String markdown) {
        if (StringUtils.isBlank(markdown)) {
            return "";
        }
        Node document = PARSER.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String content = renderer.render(document);
        content = Commons.emoji(content);

        return content;
    }

    /**
     * 替换HTML脚本
     */
    public static String cleanXss(String value) {
        //You'll need to remove the spaces from the html entities below
        value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
        value = value.replaceAll("'", "&#39;");
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        value = value.replaceAll("script", "");
        return value;
    }

    /**
     * 判断是否是合法路径
     */
    public static boolean isPath(String slug) {
        if (StringUtils.isNotBlank(slug)) {
            if (slug.contains("/") || slug.contains(" ") || slug.contains(".")) {
                return false;
            }
            Matcher matcher = SLUG_REGEX.matcher(slug);
            return matcher.find();
        }
        return false;
    }

    public static String getFileKey(String name) {
        String prefix = "/upload/" + DateKit.dateFormat(new Date(), "yyyy/MM");
        if (!new File(AttachController.CLASSPATH + prefix).exists()) {
            new File(AttachController.CLASSPATH + prefix).mkdirs();
        }

        name = StringUtils.trimToNull(name);
        if (name == null) {
            return prefix + "/" + UUID.UU32() + "." + null;
        } else {
            name = name.replace('\\', '/');
            name = name.substring(name.lastIndexOf("/") + 1);
            int index = name.lastIndexOf(".");
            String ext = null;
            if (index >= 0) {
                ext = StringUtils.trimToNull(name.substring(index + 1));
            }
            return prefix + "/" + UUID.UU32() + "." + (ext);
        }
    }

    /**
     * 判断文件是否是图片类型
     */
    public static boolean isImage(InputStream imageFile) {
        try {
            Image img = ImageIO.read(imageFile);
            return img != null && img.getWidth(null) > 0 && img.getHeight(null) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取保存文件的位置
     */
    public static String getUplodFilePath() {
        File file = new File("");
        return file.getAbsolutePath() + "/";
    }
}
