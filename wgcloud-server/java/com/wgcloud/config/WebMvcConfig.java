package com.wgcloud.config;

import com.wgcloud.interceptor.TimeConsumingInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * @ClassName:IndexController.java
 * @version v2.3
 * @author: http://www.wgstart.com
 * @date: 2019年11月16日
 * @Description: AppInfoController.java
 * @Copyright: 2019-2020 wgcloud. All rights reserved.
 *
 */
@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("forward:/login/login.html");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
	}
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new TimeConsumingInterceptor()).addPathPatterns("/**")
				.excludePathPatterns("/swagger/**", "/static/**", "/resource/**");
	}
}
