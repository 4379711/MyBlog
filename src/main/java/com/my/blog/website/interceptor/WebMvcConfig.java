package com.my.blog.website.interceptor;


import com.my.blog.website.utils.TaleUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * 添加自定义组件
 *
 * @author liuyalong
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Resource
    private BaseInterceptor baseInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(baseInterceptor);
    }

    /**
     * 添加静态资源文件，外部可以直接访问地址
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**").
                addResourceLocations("file:" + TaleUtils.getUplodFilePath() + "upload/");
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }
}
