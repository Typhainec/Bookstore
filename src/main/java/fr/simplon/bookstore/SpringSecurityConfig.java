package fr.simplon.bookstore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;


@Configuration
@EnableWebSecurity
public class SpringSecurityConfig //Cette classe et ses annotations servent à demander une authentification pour chaque page.
{
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable() // Pour l'instant, on désactive la protection CSRF
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.GET, "/").permitAll()
                .requestMatchers(HttpMethod.GET, "/book-sheet").permitAll()
                .requestMatchers(HttpMethod.GET, "/cart").authenticated()
                .requestMatchers(HttpMethod.GET, "/contact").permitAll()
                .requestMatchers(HttpMethod.GET, "/rgpd").permitAll()
                .requestMatchers(HttpMethod.POST, "/form-add-book").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.POST, "/create-account").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN") // Pas de préfixe "ROLE" mais en BDD : oui !
                .anyRequest().permitAll()
                .and().formLogin()
                .and().build();
    }


    @Autowired
    private javax.sql.DataSource dataSource;

    @Bean
    public UserDetailsManager users(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();

    }
}