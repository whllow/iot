package com.whllow.iot.config;

import com.whllow.iot.annotation.LoginRequired;
import com.whllow.iot.controller.interceptor.LoginRequireInterceptor;
import com.whllow.iot.controller.interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMVCConfig implements WebMvcConfigurer {

    @Autowired
    public LoginRequireInterceptor loginRequireInterceptor;

    @Autowired
    public LoginTicketInterceptor loginTicketInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/css/*.css","/js/*.js","/img/*.png","/img/*.jpg",
                        "/img/*.jpeg");
        registry.addInterceptor(loginRequireInterceptor)//是下面的静态资源就不拦截
                .excludePathPatterns("/css/*.css","/js/*.js","/img/*.png","/img/*.jpg",
                        "/img/*.jpeg");

    }
}
