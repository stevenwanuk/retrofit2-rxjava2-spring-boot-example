package com.sven.retrofit2.config;

import org.springframework.context.annotation.Bean;

import io.swagger.annotations.Api;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

//@Configuration
//@EnableSwagger2
public class SwaggerConfig {
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.withClassAnnotation(Api.class)).paths(PathSelectors.any()).build();
	}

	// @Bean
	// public WebMvcConfigurer webMvcConfigurer()
	// {
	// return new WebMvcConfigurer()
	// {
	// @Override
	// public void addViewControllers(final ViewControllerRegistry registry)
	// {
	// // redirect / to swagger page
	// registry.addViewController("/").setViewName(
	// "redirect:/swagger-ui.html");
	// registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
	// }
	// };
	// }
}
