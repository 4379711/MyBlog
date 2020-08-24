package com.my.blog.website.interceptor;

import com.my.blog.website.modal.Vo.UserVo;
import com.my.blog.website.service.IUserService;
import com.my.blog.website.utils.*;
import com.my.blog.website.constant.WebConst;
import com.my.blog.website.dto.Types;
import org.apache.commons.lang3.StringUtils;
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
    @Resource
    private IUserService userService;

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

        HttpSession session = request.getSession();
        //从session 中获取登录的用户
        UserVo user = TaleUtils.getLoginUser(request);

        //从cookie中获取用户ID
        Integer uid = TaleUtils.getCookieUid(request);

        //未登录后台,并且不是在登录页面
        boolean isLoginAdminAction = uri.startsWith("/admin") && !uri.startsWith("/admin/login");
        if (isLoginAdminAction) {

            //cookie  session里没有用户信息,必须去登录
            if (uid == null && user == null) {
                response.sendRedirect("/admin/login");
                return false;
            }
            // session过期,重新登录,并返回之前的页面
            //todo 这里应该使用转发,不应使用重定向
//            if (user == null) {
//                String referer = request.getHeader("Referer");
//                if (StringUtils.isNotBlank(referer) && !"/admin/login".equals(referer)) {
//                    request.getRequestDispatcher(referer).forward(request, response);
//                } else {
//                    response.sendRedirect("/admin/login");
//                }
//
//                return false;
//            }
        }

        //设置get请求的token
        if ("GET".equals(request.getMethod())) {
            String csrfToken = UUID.UU64();
            cache.hset(Types.CSRF_TOKEN.getType(), csrfToken, uri, WebConst.CSRF_TOKEN_TIMEOUT);
            request.setAttribute("_csrf_token", csrfToken);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        //一些工具类和公共方法
        httpServletRequest.setAttribute("commons", commons);
        httpServletRequest.setAttribute("adminCommons", adminCommons);
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
