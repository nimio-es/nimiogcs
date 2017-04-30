package es.nimio.nimiogcs.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import es.nimio.nimiogcs.web.api.ApiDeployerController;
import es.nimio.nimiogcs.web.controllers.HomeController;

@Configuration
@ComponentScan(basePackageClasses={HomeController.class, ApiDeployerController.class})
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class WebConfig extends WebMvcConfigurerAdapter {

	@Bean
	public ViewResolver viewResolver(SpringTemplateEngine templateEngine) {
		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		viewResolver.setTemplateEngine(templateEngine);
		return viewResolver;
	}
	
	@Bean
	public SpringTemplateEngine templateEngine() {
		
		// los localizadores de plantillas
		Set<TemplateResolver> resolvers = new HashSet<TemplateResolver>();
		resolvers.add(templateResolver());
		resolvers.add(appSpecificTemplateResolver());
		
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolvers(resolvers);
		templateEngine.addDialect(new SpringSecurityDialect());
		return templateEngine;
	}
	
	@Bean
	public TemplateResolver templateResolver() {
		SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();	
		templateResolver.setPrefix("classpath:/templates/");							// en la carpeta templates dentro de WEB-INF
		templateResolver.setSuffix(".html");										// todas las páginas con extensión html
		templateResolver.setTemplateMode("HTML5");									// html5 estricto
		templateResolver.setCacheTTLMs(1L * 60L * 60L * 1000L); 					// persisten en caché durante 1h
		templateResolver.setOrder(2);
		return templateResolver;
	}

	@Bean
	public TemplateResolver appSpecificTemplateResolver() {
		TemplateResolver templateResolver = new ServletContextTemplateResolver();	
		templateResolver.setPrefix("/WEB-INF/templates/");							// en la carpeta templates dentro de WEB-INF
		templateResolver.setSuffix(".html");										// todas las páginas con extensión html
		templateResolver.setTemplateMode("HTML5");									// html5 estricto
		templateResolver.setCacheTTLMs(1L * 60L * 60L * 1000L); 					// persisten en caché durante 1h
		templateResolver.setOrder(1);												// primero buscaremos siempre en la propia aplicación
		return templateResolver;
	}
	
	@Override
	public void configureDefaultServletHandling(
			DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/res/**")
					.addResourceLocations("/WEB-INF/resources/");
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
	    registry.addViewController("/login").setViewName("login");
	    registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
	}
}
