package com.ruoyi.framework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 通用配置
 *
 * @author Lion Li
 */
@Configuration
public class ResourcesConfig implements WebMvcConfigurer {

    // @Override
    // public void addInterceptors(InterceptorRegistry registry) {
    //     // 全局访问性能拦截
    //     registry.addInterceptor(new PlusWebInvokeTimeInterceptor());
    // }
    //
    // @Override
    // public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // }

    /**
     * 跨域配置
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // 设置访问源地址
        config.addAllowedOriginPattern("http://localhost:*");
        config.addAllowedOriginPattern("http://127.0.0.1:*");
        config.addAllowedOriginPattern("http://192.168.*:*");
        config.addAllowedOriginPattern("http://10.*:*");
        config.addAllowedOriginPattern("http://172.16.*:*");
        config.addAllowedOriginPattern("http://172.17.*:*");
        config.addAllowedOriginPattern("http://172.18.*:*");
        config.addAllowedOriginPattern("http://172.19.*:*");
        config.addAllowedOriginPattern("http://172.20.*:*");
        config.addAllowedOriginPattern("http://172.21.*:*");
        config.addAllowedOriginPattern("http://172.22.*:*");
        config.addAllowedOriginPattern("http://172.23.*:*");
        config.addAllowedOriginPattern("http://172.24.*:*");
        config.addAllowedOriginPattern("http://172.25.*:*");
        config.addAllowedOriginPattern("http://172.26.*:*");
        config.addAllowedOriginPattern("http://172.27.*:*");
        config.addAllowedOriginPattern("http://172.28.*:*");
        config.addAllowedOriginPattern("http://172.29.*:*");
        config.addAllowedOriginPattern("http://172.30.*:*");
        config.addAllowedOriginPattern("http://172.31.*:*");
        // 设置访问源请求头
        config.addAllowedHeader("Authorization");
        config.addAllowedHeader("Content-Type");
        config.addAllowedHeader("X-Requested-With");
        config.addAllowedHeader("Accept");
        config.addAllowedHeader("Origin");
        config.addAllowedHeader("Access-Control-Request-Method");
        config.addAllowedHeader("Access-Control-Request-Headers");
        // 设置访问源请求方法
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");
        // 有效期 1800秒
        config.setMaxAge(1800L);
        // 添加映射路径，拦截一切请求
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        // 返回新的CorsFilter
        return new CorsFilter(source);
    }
}
