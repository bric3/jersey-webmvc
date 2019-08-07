package com.github.bric3.jerseywebmvc;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration {

    @RestController
    static class DocEndpoint {
        @RequestMapping(method = RequestMethod.GET,
                        value = "/rest/",
                        produces = "application/json")
        public Structure index() {
            return new Structure();
        }
    }

    @Bean
    WebMvcConfigurer docForwarder() {
        return new WebMvcConfigurer() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/doc/")
                        .setViewName("forward:/doc/index.html");
            }
        };
    }
}
