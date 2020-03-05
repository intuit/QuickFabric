package com.intuit.quickfabric.commons.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.intuit.quickfabric.commons.constants.ApiUrls;
import com.intuit.quickfabric.commons.helper.ConfigHelper;
 

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter 
{
    @Autowired
    JWTTokenProvider jwtTokenProvider;
    
    @Autowired
    ConfigHelper configHelper;
    
   @Override
    protected void configure (HttpSecurity httpSecurity) throws Exception 
    
    {
	 	// Disable CORS (cross origin resource sharing) and CSRF (cross site request forgery)
	 	httpSecurity.csrf().disable();

        // No session will be created or used by spring security
    	httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Entry points
    	httpSecurity.authorizeRequests()
            .antMatchers("/actuator/**").hasAnyAuthority("superadmin")
            .antMatchers(ApiUrls.SSO_REDIRECT_PATH).permitAll()
            .antMatchers(ApiUrls.GET_USER_ROLES_SERVICE_PATH).permitAll()
            .antMatchers(ApiUrls.LOGIN_SERVICE_PATH).permitAll()
            .anyRequest().authenticated();

        // Apply JWT
    	httpSecurity.apply(new JwtTokenFilterConfigurer(jwtTokenProvider));
    }
    
    @Override
    public void configure(WebSecurity webSecurity) 
    {
    	webSecurity.ignoring()
                .antMatchers("/v2/api-docs")
                .antMatchers("/swagger-resources/**")
                .antMatchers("/swagger-ui.html")
                .antMatchers("/configuration/**")
                .antMatchers("/webjars/**")
                .antMatchers("/public");
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
    }
}
