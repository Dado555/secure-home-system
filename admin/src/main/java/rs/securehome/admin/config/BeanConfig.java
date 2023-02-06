package rs.securehome.admin.config;


import org.drools.core.ClockType;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableAsync
public class BeanConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                        .addMapping("/**")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS")
                        .allowedOrigins("http://localhost:8080")
                        .allowCredentials(true);
            }
        };
    }

    @Bean
    public KieSession kieContainer() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks
                .newKieContainer(ks.newReleaseId("rs.securehome", "rules", "1.0.0-SNAPSHOT"));
        KieBaseConfiguration kieBaseConfiguration = ks.newKieBaseConfiguration();
        kieBaseConfiguration.setOption(EventProcessingOption.STREAM);
        KieBase kieBase = kContainer.newKieBase(kieBaseConfiguration);

        KieSessionConfiguration kieSessionConfiguration = ks.newKieSessionConfiguration();
        kieSessionConfiguration.setOption(ClockTypeOption.get(ClockType.REALTIME_CLOCK.getId()));
        return kieBase.newKieSession(kieSessionConfiguration, null);
    }

    @Bean
    public List<String> maliciousIps() throws IOException, URISyntaxException{
        List<String> result;
        try(Stream<String> lines = Files.lines(Paths.get(ClassLoader.getSystemResource("maliciousIps.txt").toURI())))  {
            result = lines.collect(Collectors.toList());
        }
        return result;
    }
}
