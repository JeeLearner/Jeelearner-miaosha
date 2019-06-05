package com.jeelearn.mymiaosha.config;

import java.util.List;

import com.jeelearn.mymiaosha.access.AccessInterceptor;
import com.jeelearn.mymiaosha.access.AccessLimit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter{

	@Autowired
	private UserArgumentResolver userArgumentResolver;

	@Autowired
	private AccessInterceptor accessInterceptor;

	/**
	 * 自定义HandlerMethodArgumentResolver
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(userArgumentResolver);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(accessInterceptor);
	}
	
}
