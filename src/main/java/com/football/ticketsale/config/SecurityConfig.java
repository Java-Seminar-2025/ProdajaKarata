package com.football.ticketsale.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
//                .csrf(csrf -> csrf.disable()
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/home", "/signup/**", "/register/**", "/welcome", "/css/**", "/js/**", "/api/**").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/matches/**").permitAll()
                        .requestMatchers("/checkout/**", "/my_tickets").permitAll()   //.authenticated()
                        .requestMatchers("/api/admin/**").permitAll()  //.authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/home", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/welcome?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID") // ovo spring sam stvara
                        .permitAll()
                );

        return http.build();
    }
}