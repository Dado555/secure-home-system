package rs.securehome.admin.util.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.web.filter.OncePerRequestFilter;
import rs.securehome.admin.exception.BlacklistedJWTException;
import rs.securehome.admin.repository.BlacklistedJWTRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
public abstract class JwtFilter extends OncePerRequestFilter {

    @Value("${jwt.header.string}")
    public String headerString;

    @Value("${jwt.token.prefix}")
    public String tokenPrefix;

    private final JwtUtil tokenUtil;
    private final UserDetailsService userDetailsService;
    private final BlacklistedJWTRepository blacklistedJWTRepository;

    protected JwtFilter(JwtUtil tokenUtil, UserDetailsService userDetailsService, BlacklistedJWTRepository blacklistedJWTRepository) {
        this.tokenUtil = tokenUtil;
        this.userDetailsService = userDetailsService;
        this.blacklistedJWTRepository = blacklistedJWTRepository;
    }

    @Override
    protected abstract void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException;


    protected String getCookieValue(Cookie[] cookies, String cookieName) {
        var optionalCookie = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(cookieName))
                .findFirst();
        if (optionalCookie.isEmpty())
            return null;
        return optionalCookie.get().getValue();
    }

    protected String getJwtFromRequestHeader(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader(headerString);
        if (authorizationHeader == null)
            return null;
        else if (authorizationHeader.startsWith(tokenPrefix))
            return authorizationHeader.replace(tokenPrefix, "");
        return null;
    }


    protected void checkTokenOnBlacklist(String jwt) {
        var blacklistedJWT = this.blacklistedJWTRepository.findBlacklistedJWTByJwt(jwt);
        if (blacklistedJWT.isPresent())
            throw new BlacklistedJWTException("Token has been revoked.");
    }


    protected void validateToken(String jwt, String cookieValue) {
        try {
            String username = tokenUtil.extractUsernameFromToken(jwt);

            var res = tokenUtil.checkAlgHeaderParam(jwt);
            if (!res)
                throw new MalformedJwtException(String.format("JWT signature algorithm is not %s.", JwtUtil.signatureAlgorithm.getValue()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (Boolean.TRUE.equals(tokenUtil.validateToken(jwt, userDetails, cookieValue))) {
                var authenticationToken = new TokenBasedAuthentication(userDetails);
                authenticationToken.setToken(jwt);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (IllegalArgumentException |
                SignatureException |
                ExpiredJwtException |
                MalformedJwtException |
                UnsupportedJwtException |
                UsernameNotFoundException |
                InvalidCookieException |
                BlacklistedJWTException ignored) {
            log.error(ignored.getMessage());
        }
    }

    protected void validateToken(String jwt) {
        try {
            String username = tokenUtil.extractUsernameFromToken(jwt);

            var res = tokenUtil.checkAlgHeaderParam(jwt);
            if (!res)
                throw new MalformedJwtException(String.format("JWT signature algorithm is not %s.", JwtUtil.signatureAlgorithm.getValue()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (Boolean.TRUE.equals(tokenUtil.validateToken(jwt, userDetails))) {
                var authenticationToken = new TokenBasedAuthentication(userDetails);
                authenticationToken.setToken(jwt);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (IllegalArgumentException |
                SignatureException |
                ExpiredJwtException |
                MalformedJwtException |
                UnsupportedJwtException |
                UsernameNotFoundException |
                InvalidCookieException |
                BlacklistedJWTException ignored) {
            log.error(ignored.getMessage());
        }
    }


}
