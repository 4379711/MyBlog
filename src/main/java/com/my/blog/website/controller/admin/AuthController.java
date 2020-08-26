package com.my.blog.website.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.blog.website.constant.WebConst;
import com.my.blog.website.controller.BaseController;
import com.my.blog.website.dto.LogActions;
import com.my.blog.website.exception.TipException;
import com.my.blog.website.model.Bo.RestResponseBo;
import com.my.blog.website.model.Vo.UserVo;
import com.my.blog.website.service.ILogService;
import com.my.blog.website.service.IUserService;
import com.my.blog.website.utils.Commons;
import com.my.blog.website.utils.TaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 用户后台登录/登出,修改密码
 *
 * @author liuyalong
 */
@Controller
@RequestMapping("/admin")
@Transactional(rollbackFor = TipException.class)
public class AuthController extends BaseController {


    @Resource
    private IUserService userService;

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
            UserVo user = userService.login(username, password);
            HttpSession session = request.getSession();
            // 设置session过期时间
            session.setMaxInactiveInterval(WebConst.SESSION_TIMEOUT);
            session.setAttribute(WebConst.LOGIN_SESSION_KEY, user);

            // 记住登录状态勾选时,把用户id放到cookie
            if (rememberMe == 1) {
                ObjectMapper objectMapper = new ObjectMapper();
                String userString = objectMapper.writeValueAsString(user);
                TaleUtils.setCookie(response, userString);
            }

            logService.insertLog(LogActions.LOGIN.getAction(), null, request.getRemoteAddr(), user.getUid());
        } catch (Exception e) {
            errorCount = null == errorCount ? 1 : errorCount + 1;
            if (errorCount > WebConst.ERROR_PASSWORD_TIMES) {
                return RestResponseBo.fail("您输入密码已经错误超过3次，请30分钟后尝试");
            }
            cache.set("login_error_count", errorCount, WebConst.ERROR_PASSWORD_TIMEOUT);
            String msg = "登录失败";
            return RestResponseBo.fail(msg);
        }

        return RestResponseBo.ok("登录成功");
    }

    /**
     * 注销
     */
    @RequestMapping("/logout")
    public void logout(HttpServletResponse response, HttpServletRequest request) {
        //销毁session：
        request.getSession(false).invalidate();

        //清除cookie：
        Cookie cookie = new Cookie(WebConst.USER_IN_COOKIE, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        try {
            response.sendRedirect(Commons.site_login());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 修改密码
     */
    @PostMapping(value = "/password")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo upPwd(@RequestParam String oldPassword, @RequestParam String password, HttpServletRequest request, HttpSession session) {
        UserVo users = this.user(request);
        if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(password)) {
            return RestResponseBo.fail("请确认信息输入完整");
        }

        if (!users.getPassword().equals(TaleUtils.mD5encode(users.getUsername() + oldPassword))) {
            return RestResponseBo.fail("旧密码错误");
        }
        if (password.length() < 6 || password.length() > 14) {
            return RestResponseBo.fail("请输入6-14位密码");
        }

        try {
            UserVo temp = new UserVo();
            temp.setUid(users.getUid());
            String pwd = TaleUtils.mD5encode(users.getUsername() + password);
            temp.setPassword(pwd);
            userService.updateByUid(temp);
            logService.insertLog(LogActions.UP_PWD.getAction(), null, request.getRemoteAddr(), this.getUid(request));

            //更新session中的数据
            UserVo original = (UserVo) session.getAttribute(WebConst.LOGIN_SESSION_KEY);
            original.setPassword(pwd);
            session.setAttribute(WebConst.LOGIN_SESSION_KEY, original);
            return RestResponseBo.ok();
        } catch (Exception e) {
            String msg = "密码修改失败";
            return RestResponseBo.fail(msg);
        }
    }
}
