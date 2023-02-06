package rs.securehome.house.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
@EnableAsync
public class BeanConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                        .addMapping("/**")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS")
                        .allowedOrigins("http://localhost:8083")
                        .allowCredentials(true);
            }
        };
    }

    @Bean
    public SecretKey secretKeyConfigurer() {
        byte[] decodedKey = Base64.getDecoder().decode("MO3tr1rvVi3uGOTrcYiGmuzCO1l+okp3N4QVDPdf7Xg=");
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }
}
