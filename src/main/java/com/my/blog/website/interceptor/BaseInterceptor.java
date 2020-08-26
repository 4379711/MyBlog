package com.my.blog.website.interceptor;

import com.my.blog.website.model.Vo.UserVo;
import com.my.blog.website.utils.*;
import com.my.blog.website.constant.WebConst;
import com.my.blog.website.dto.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 自定义拦截器
 *
 * @author liuyalong
 */
@Component
public class BaseInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(BaseInterceptor.class);

    private MapCache cache = MapCache.single();

    @Resource
    private Commons commons;

    @Resource
    private AdminCommons adminCommons;

    /***
     * 请求拦截处理
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String uri = request.getRequestURI();
        logger.info("用户访问地址: {}, 来路地址: {}", uri, IPKit.getIpAddrByRequest(request));

        //放行静态文件
        if (uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".png") || uri.endsWith(".jpg")) {
            return true;
        }
        //获取登录的用户
        UserVo user = TaleUtils.getLoginUser(request);

        //后台管理页面需要拦截
        boolean isLoginAdminAction = uri.startsWith("/admin") && !uri.startsWith("/admin/login");

        if (isLoginAdminAction) {
            //cookie 里没有用户信息,必须去登录
            if (user == null) {
                // 这里实际上有问题,如果是ajax请求是无法重定向的,所以还需要特殊处理,暂时懒得做
                response.sendRedirect("/admin/login");
                return false;
            } else {
                // 退出浏览器会新生成session,此时把cookies里的用户信息给新的session
                HttpSession session = request.getSession(false);
                if (null == session) {
                    HttpSession newSession = request.getSession();
                    newSession.setAttribute(WebConst.LOGIN_SESSION_KEY, user);
                    newSession.setMaxInactiveInterval(WebConst.SESSION_TIMEOUT);
                }
            }
        }

        //设置get请求的token
        if ("GET".equals(request.getMethod())) {
            String csrfToken = UUID.uU64();
            cache.hset(Types.CSRF_TOKEN.getType(), csrfToken, uri, WebConst.CSRF_TOKEN_TIMEOUT);
            request.setAttribute("_csrf_token", csrfToken);
        }
        return true;

    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) {
        //一些工具类和公共方法
        httpServletRequest.setAttribute("commons", commons);
        httpServletRequest.setAttribute("adminCommons", adminCommons);
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {

    }
}
