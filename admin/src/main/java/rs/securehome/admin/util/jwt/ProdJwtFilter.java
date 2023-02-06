package rs.securehome.admin.util.jwt;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import rs.securehome.admin.repository.BlacklistedJWTRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
@Profile("production")
public class ProdJwtFilter extends JwtFilter {


    @Autowired
    public ProdJwtFilter(JwtUtil tokenUtil, UserDetailsService userDetailsService, BlacklistedJWTRepository blacklistedJWTRepository) {
        super(tokenUtil, userDetailsService, blacklistedJWTRepository);
        log.info("ProdJwtFilter creation");
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            String cookieValue = this.getCookieValue(cookies, "jwt-security");
            String jwt = this.getJwtFromRequestHeader(request);
            if (cookieValue != null && jwt != null) {
                this.checkTokenOnBlacklist(jwt);
                this.validateToken(jwt, cookieValue);
            }

        }
        filterChain.doFilter(request, response);
    }
}
