package com.svrij22.main.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public final static String LOGIN_PATH = "/login";
    public final static String REGISTER_PATH = "/register";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().configurationSource(corsConfigurationSource()).and()
                .csrf().disable()
                .authorizeRequests()
                //.antMatchers(HttpMethod.POST, REGISTER_PATH).permitAll()
                //.antMatchers(HttpMethod.POST, LOGIN_PATH).permitAll()
                //.antMatchers(HttpMethod.GET, "/locaties/id/**").permitAll()
                //.antMatchers(HttpMethod.POST, "/locaties/zoek").permitAll()

                .antMatchers("/**").permitAll()
                .antMatchers("/image/**").permitAll()
                .antMatchers("/js/**", "/css/**", "/img/**","/#/**").permitAll();

                //.anyRequest().authenticated()
                // .and()
                // .addFilterBefore(
                //         new JwtAuthenticationFilter(
                //                 LOGIN_PATH,
                //                 this.jwtSecret,
                //                 this.jwtExpirationInMs,
                //                 this.authenticationManager()
                //         ),
                //         UsernamePasswordAuthenticationFilter.class
                // )
                // .addFilter(new JwtAuthorizationFilter(this.jwtSecret, this.authenticationManager()))
                //.sessionManagement()
                //.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        corsConfiguration.addExposedHeader("Authorization");
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}