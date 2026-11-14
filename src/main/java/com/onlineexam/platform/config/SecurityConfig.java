package com.onlineexam.platform.config;

import com.onlineexam.platform.entity.UserRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/").permitAll()
                                .requestMatchers("/login").permitAll()
                                .requestMatchers("/error").permitAll()
                                .requestMatchers("/favicon.ico").permitAll()
                                .requestMatchers("/image/**").permitAll()
                                .requestMatchers("/css/**").permitAll()
                                .requestMatchers("/admin/**").hasRole(UserRole.ADMIN.name())
                                .requestMatchers("/lecturer/**").hasRole(UserRole.LECTURER.name())
                                .requestMatchers("/student/**").hasRole(UserRole.STUDENT.name())
                                .anyRequest().authenticated()
                ).formLogin(form -> form.loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(form -> form.logoutSuccessUrl("/login?logout")
                        .permitAll());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .roles(UserRole.ADMIN.name())
                .build();
        UserDetails lecturer = User.builder()
                .username("lecturer")
                .password(passwordEncoder.encode("lecturer"))
                .roles(UserRole.LECTURER.name())
                .build();
        UserDetails student = User.builder()
                .username("student")
                .password(passwordEncoder.encode("student"))
                .roles(UserRole.STUDENT.name())
                .build();
        return new InMemoryUserDetailsManager(admin, lecturer, student);
    }
}
