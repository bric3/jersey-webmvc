package com.github.bric3.jerseywebmvc.spring_configuration_mitigation;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo.BuilderConfiguration;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;
import java.util.List;
import java.util.Objects;

import static org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME;

@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@AutoConfigureBefore(DispatcherServletAutoConfiguration.class)
@ConditionalOnClass(ServletRegistration.class)
@EnableConfigurationProperties(WebMvcProperties.class)
@Profile("web-mvc-config-hack-mitigation")
public class DispatcherConfiguration {
    private final WebMvcProperties webMvcProperties;
    private final MultipartConfigElement multipartConfig;

    static {
        // has to be done before WebMvcEndpointHandlerMapping.afterPropertiesSet
        ReflectionUtils.doWithFields(WebMvcEndpointHandlerMapping.class,
                                     field -> {
                                         ReflectionUtils.makeAccessible(field);
                                         final RequestMappingInfo.BuilderConfiguration builderConfiguration = (BuilderConfiguration) ReflectionUtils.getField(field, null);
                                         builderConfiguration.setUrlPathHelper(urlPathHelper());
                                     },
                                     field -> Objects.equals("builderConfig", field.getName()));
    }

    public DispatcherConfiguration(WebMvcProperties webMvcProperties,
                                   ObjectProvider<MultipartConfigElement> multipartConfigProvider) {
        this.webMvcProperties = webMvcProperties;
        this.multipartConfig = multipartConfigProvider.getIfAvailable();
    }

    @Bean
    WebMvcConfigurer useFullPath() {
        return new WebMvcConfigurer() {
            @Override
            public void configurePathMatch(PathMatchConfigurer configurer) {
                final UrlPathHelper urlPathHelper = urlPathHelper();
                configurer.setUrlPathHelper(urlPathHelper);
            }
        };
    }

    @Bean
    Object handlerMappingCustomizer(List<AbstractHandlerMapping> handlerMappings) {
        final UrlPathHelper urlPathHelper = urlPathHelper();
        handlerMappings.forEach(handlerMapping -> handlerMapping.setUrlPathHelper(urlPathHelper));
        return true;
    }

    private static UrlPathHelper urlPathHelper() {
        final UrlPathHelper urlPathHelper = new UrlPathHelper();
        urlPathHelper.setAlwaysUseFullPath(true);
        return urlPathHelper;
    }

    @Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME)
    public ServletRegistrationBean<DispatcherServlet> dispatcherServletRegistration(DispatcherServlet dispatcherServlet) {
        ServletRegistrationBean<DispatcherServlet> registration =
                new ServletRegistrationBean<>(dispatcherServlet,
                                              "/rest/*",
                                              "/doc/*",
                                              "/actuator/*",
                                              "/error/*",
                                              "/favicon.ico");
        registration.setName(DEFAULT_DISPATCHER_SERVLET_BEAN_NAME);
        registration.setLoadOnStartup(this.webMvcProperties.getServlet().getLoadOnStartup());
        if (this.multipartConfig != null) {
            registration.setMultipartConfig(this.multipartConfig);
        }
        return registration;
    }

    @Bean
    DispatcherServletPath dispatcherServletPath() {
        final String path = this.webMvcProperties.getServlet().getPath();
        return () -> path;
    }
}
