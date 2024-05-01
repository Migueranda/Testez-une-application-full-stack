package com.openclassrooms.starterjwt.services;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Configuration
@EnableWebMvc
public class TestWebMvcConfig implements WebMvcConfigurer {
   @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add((request, response, handler, ex) -> {
            if (ex instanceof BadCredentialsException) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Set HTTP 401 status
                return new ModelAndView(); // Return an empty ModelAndView to signal that the exception has been handled
            }
            return null; // Let other exceptions be handled by default resolver
        });
    }
}
