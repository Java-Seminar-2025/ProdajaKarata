package com.football.ticketsale.config;


import com.football.ticketsale.security.CustomLoginSuccessHandler;
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
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(auth -> auth


// 🌍 PUBLIC
                                .requestMatchers(
                                        "/", "/home", "/welcome",
                                        "/login", "/signup/**", "/register/**",
                                        "/css/**", "/js/**"
                                ).permitAll()


                                .requestMatchers(
                                        org.springframework.http.HttpMethod.GET,
                                        "/api/matches/**"
                                ).permitAll()


// 👤 USER + ADMIN
                                .requestMatchers("/checkout/**", "/my-tickets")
                                .hasAnyRole("USER", "ADMIN")


// 🔐 ADMIN ONLY
                                .requestMatchers("/admin/**", "/api/admin/**")
                                .hasRole("ADMIN")


// 🔒 EVERYTHING ELSE
                                .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(new CustomLoginSuccessHandler())
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/welcome?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );


        return http.build();
    }
}