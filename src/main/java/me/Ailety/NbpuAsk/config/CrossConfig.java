package me.Ailety.NbpuAsk.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;

@Configuration
public class CrossConfig {

    @Bean
    public FilterRegistrationBean<?> corsFilter() {

        CorsConfiguration corsConfiguration = getCorsConfiguration();

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        FilterRegistrationBean<?> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE); // 优先级

        return bean;
    }

    private static @NotNull CorsConfiguration getCorsConfiguration() {

        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // 1.允许任何来源
        corsConfiguration.setAllowedOriginPatterns(Collections.singletonList("*"));
        // 2.允许任何请求头
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
        // 3.允许任何方法
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
        // 4.预检请求的缓存时间
        corsConfiguration.setMaxAge(1800L);
        // 5.允许凭证
        corsConfiguration.setAllowCredentials(true);

        return corsConfiguration;

    }

}
