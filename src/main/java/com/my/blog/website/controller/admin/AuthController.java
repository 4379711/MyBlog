package com.my.blog.website.controller.admin;

import com.my.blog.website.constant.WebConst;
import com.my.blog.website.controller.BaseController;
import com.my.blog.website.dto.LogActions;
import com.my.blog.website.exception.TipException;
import com.my.blog.website.modal.Bo.RestResponseBo;
import com.my.blog.website.modal.Vo.UserVo;
import com.my.blog.website.service.ILogService;
import com.my.blog.website.service.IUserService;
import com.my.blog.website.utils.Commons;
import com.my.blog.website.utils.TaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * 用户后台登录/登出
 *
 * @author liuyalong
 */
@Controller
@RequestMapping("/admin")
@Transactional(rollbackFor = TipException.class)
public class AuthController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Resource
    private IUserService usersService;

    @Resource
    private ILogService logService;


    @GetMapping(value = "/login")
    public String login() {
        return "admin/login";
    }

    /**
     * 管理后台登录
     */
    @PostMapping(value = "login")
    @ResponseBody
    public RestResponseBo doLogin(@RequestParam(value = "username") String username,
                                  @RequestParam(value = "password") String password,
                                  @RequestParam(value = "remember_me", required = false, defaultValue = "0") int rememberMe,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {

        Integer errorCount = cache.get("login_error_count");

        try {
            UserVo user = usersService.login(username, password);
            HttpSession session = request.getSession();
            // 设置session过期时间
            session.setMaxInactiveInterval(WebConst.SESSION_TIMEOUT);
            session.setAttribute(WebConst.LOGIN_SESSION_KEY, user);

            // 记住登录状态勾选时,把用户id放到cookie
            if (rememberMe == 1) {
                TaleUtils.setCookie(response, user.getUid());
            }

            logService.insertLog(LogActions.LOGIN.getAction(), null, request.getRemoteAddr(), user.getUid());
        } catch (Exception e) {
            errorCount = null == errorCount ? 1 : errorCount + 1;
            if (errorCount > WebConst.ERROR_PASSWORD_TIMES) {
                return RestResponseBo.fail("您输入密码已经错误超过3次，请30分钟后尝试");
            }
            cache.set("login_error_count", errorCount, WebConst.ERROR_PASSWORD_TIMEOUT);
            String msg = "登录失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }



        //todo 这里应该跳转回之前的页面,不应该直接去首页
        //登录成功后跳转
//        String referer = request.getHeader("Referer");
        //默认跳转到首页
//        if (StringUtils.isBlank(referer) || referer.endsWith("/admin/login")) {
//            referer = "/";
//        } else {
//            String[] split = referer.split("/admin");
//            int length = split.length - 1;
//            referer = length == 0 ? "/" : split[length];
//            if (!referer.startsWith("/")) {
//                referer = "/" + referer;
//            }
//        }
        return RestResponseBo.ok("登录成功");
    }

    /**
     * 注销
     */
    @RequestMapping("/logout")
    public void logout(HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        session.removeAttribute(WebConst.LOGIN_SESSION_KEY);
        Cookie cookie = new Cookie(WebConst.USER_IN_COOKIE, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        try {
            response.sendRedirect(Commons.site_login());
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("注销失败", e);
        }
    }
}
