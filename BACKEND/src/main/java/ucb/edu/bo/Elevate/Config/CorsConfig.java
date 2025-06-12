package ucb.edu.bo.Elevate.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class CorsConfig {

    @SuppressWarnings({ "deprecation", "removal" })
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Desactiva la protección CSRF si es necesario
            .authorizeRequests(authorizeRequests -> authorizeRequests.requestMatchers("/api/v1/**").permitAll()  // Permite el acceso a los endpoints de /api/v1/user sin autenticación
            .anyRequest().authenticated()  // Requiere autenticación para cualquier otra solicitud
            )
            .cors();  // Activa CORS

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        // SOLO permite tu dominio de frontend en producción
        config.addAllowedOrigin("http://localhost:5173"); // Cambia por tu dominio real
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true); // Si usas cookies/sesiones
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
