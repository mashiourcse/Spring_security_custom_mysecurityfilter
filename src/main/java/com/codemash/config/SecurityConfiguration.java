package com.codemash.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.codemash.filter.MySecurityFilter;
import com.codemash.service.MyUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(authConfig -> {
				authConfig.requestMatchers(HttpMethod.GET, "/").permitAll();
				authConfig.requestMatchers(HttpMethod.GET, "/user").hasAnyAuthority("ROLE_USER");
				authConfig.requestMatchers(HttpMethod.GET, "/admin").hasRole("ADMIN");
				authConfig.anyRequest().authenticated();
			})
			.addFilterBefore(new MySecurityFilter(), UsernamePasswordAuthenticationFilter.class)
			.formLogin(withDefaults()) // Login with browser and Build in Form
			.httpBasic(withDefaults()); // Login with Insomnia or Postman and Basic Auth
		return http.build();
	}
		
	@Bean
	UserDetailsService myUserDetailsService() {
		return new MyUserDetailsService();
	}
	
	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	ApplicationListener<AuthenticationSuccessEvent> successEvent() {
		return event -> {
			System.err.println("Success Login " + event.getAuthentication().getClass().getName() + " - " + event.getAuthentication().getName());
		};
	}
	
	@Bean
	ApplicationListener<AuthenticationFailureBadCredentialsEvent> failureEvent() {
		return event -> {
			System.err.println("Bad Credentials Login " + event.getAuthentication().getClass().getName() + " - " + event.getAuthentication().getName());
		};
	}
}