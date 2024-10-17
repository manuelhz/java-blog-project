package org.bytebounty.app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Autowired
	private AuthenticationSuccessHandler myAuthenticationSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http			
			.authorizeHttpRequests(requests -> requests
				.requestMatchers(
                    "/css/**",
					"/img/**",
					"/js/**",
					"/lib/**",
					"/mail/**",
					"/scss/**")
                        .permitAll()
				.requestMatchers("/login")
					.permitAll()
				.requestMatchers(
                    "/",
					"/register")
                        .permitAll()
				.requestMatchers(
                    "/profile/**")
					.authenticated()
				.requestMatchers("/admin/**")
					.hasAuthority("ROLE_ADMIN")
				//.anyRequest().authenticated()
				.anyRequest().permitAll()				
			)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
			.formLogin(
				form -> form
				.loginPage("/login")
				.loginProcessingUrl("/login")
				.usernameParameter("email")
				.passwordParameter("password")
				//.defaultSuccessUrl("/", true)
				.failureUrl("/login?error")
				.successHandler(myAuthenticationSuccessHandler)				
				.permitAll())
			.logout(logout -> logout
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
        		.logoutSuccessUrl("/")
				.deleteCookies("JSESSIONID")
			)
			.rememberMe(remember -> remember
				.key("uniqueAndSecret")
				.tokenValiditySeconds(86400)
				.rememberMeParameter("remember-me"))
			.httpBasic(withDefaults())
			.csrf(AbstractHttpConfigurer::disable)
			.headers(headers ->
			headers.xssProtection(
					xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
			).contentSecurityPolicy(
					cps -> cps.policyDirectives(
						"script-src 'self' https: 'unsafe-inline'; style-src 'self' https: 'unsafe-inline'")						
			));
        		
		
		return http.build();
	}
}