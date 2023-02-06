package rs.securehome.admin.util.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import rs.securehome.admin.repository.BlacklistedJWTRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
@Slf4j
@Profile("development")
public class DevJwtFilter extends JwtFilter {


    public DevJwtFilter(JwtUtil tokenUtil, UserDetailsService userDetailsService, BlacklistedJWTRepository blacklistedJWTRepository) {
        super(tokenUtil, userDetailsService, blacklistedJWTRepository);
        log.info("DevJwtFilter creation");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = this.getJwtFromRequestHeader(request);
        if (jwt != null) {
            this.checkTokenOnBlacklist(jwt);
            this.validateToken(jwt);
        }

        filterChain.doFilter(request, response);
    }
}
