package ru.skypro.homework.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

import static org.springframework.security.config.Customizer.withDefaults;

@TestConfiguration
@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
    @Value("${spring.datasource.url}")
    private String jdbcURl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    private static final String[] AUTH_WHITELIST = {
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/webjars/**",
            "/login", "/register",
            "/ads",
            "/ads/*/image",
            "/users/*/image"
    };

    @Bean
    public DataSource dataSource() {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.h2.Driver");
        dataSourceBuilder.url(jdbcURl);
        dataSourceBuilder.username(dbUsername);
        dataSourceBuilder.password(dbPassword);
        return dataSourceBuilder.build();
    }

    @Bean
    public JdbcUserDetailsManager jdbcUserDetailsManager() {
        return new JdbcUserDetailsManager(dataSource());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(authz ->
                        authz
                                .mvcMatchers(AUTH_WHITELIST).permitAll()
                                .mvcMatchers("/ads/**", "/users/**").authenticated()
                )
                .cors()
                .and().httpBasic(withDefaults());
        return http.build();
    }
}

