package it.smartcommunitylab.cartella.asl.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import it.smartcommunitylab.cartella.asl.security.AuthorizationManager;

/*
 * extend WebMvcConfigurerAdapter and not use annotation @EnableMvc to permit correct static
 * resources publishing and restController functionalities
 */
@Configuration
public class AppConfig implements WebMvcConfigurer {

	
	@Value("${storage.local.dir}")
	private String storageDir;			
	
	private static final String[] RESOURCE_LOCATIONS = {
			"classpath:/META-INF/resources/", "classpath:/resources/",
			"classpath:/templates/", "classpath:/public/" };
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
		.allowedMethods("PUT", "DELETE", "GET", "POST", "PATCH")
		.allowedOrigins("*");
	}	
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		if (!registry.hasMappingForPattern("/webjars/**")) {
			registry.addResourceHandler("/webjars/**").addResourceLocations(
					"classpath:/META-INF/resources/webjars/");
		}
		if (!registry.hasMappingForPattern("/**")) {
			registry.addResourceHandler("/**").addResourceLocations(
					RESOURCE_LOCATIONS);
		}
		
        registry
        .addResourceHandler("/files/**")
        .addResourceLocations("file:/" + storageDir + "/");
      
      registry.addResourceHandler("swagger-ui.html")
      .addResourceLocations("classpath:/META-INF/resources/");
      registry.addResourceHandler("/webjars/**")
      .addResourceLocations("classpath:/META-INF/resources/webjars/");    			
		
	}
	
//	@Override
//	public void addViewControllers(ViewControllerRegistry registry) {
//	    registry.addViewController("/asl-azienda").setViewName("redirect:/asl-azienda/");
//	    registry.addViewController("/asl-azienda/").setViewName("asl-azienda/index");
//	    
//	    registry.addViewController("/asl-scuola").setViewName("redirect:/asl-scuola/");
//	    registry.addViewController("/asl-scuola/").setViewName("asl-scuola/index");
//	    
//	    registry.addViewController("/asl-studente").setViewName("redirect:/asl-studente/");
//	    registry.addViewController("/asl-studente/").setViewName("asl-studente/index");
//
//	    registry.addViewController("/asl-ruoli").setViewName("redirect:/asl-ruoli/");
//	    registry.addViewController("/asl-ruoli/").setViewName("asl-ruoli/index");	    
//	    
//	    registry.addViewController("/asl-login").setViewName("redirect:/asl-login/");
//	    registry.addViewController("/asl-login/").setViewName("asl-login/index");
//	    
//	    registry.addViewController("").setViewName("redirect:/asl-login/");
//	    registry.addViewController("/").setViewName("redirect:/asl-login/");
//	}

    
	@Bean
	AuthorizationManager getAuthorizationManager() {
		return new AuthorizationManager();
	}    	
	
	@Bean
	public OncePerRequestFilter noContentFilter() {
		return new OncePerRequestFilter() {
			
			 @Override
			    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
			        filterChain.doFilter(httpServletRequest, httpServletResponse);
			        if (httpServletResponse.getContentType() == null ||
			                httpServletResponse.getContentType().equals("")) {
			            httpServletResponse.setStatus(HttpStatus.NO_CONTENT.value());
			        }
			    }
		};
	}	
	
}
