package rs.securehome.house.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;


@Component
public class HttpRequestUtil {


    @Value("${jwt.header.string}")
    private String headerString;


    @Value("${jwt.token.prefix}")
    private String tokenPrefix;


    public String getCookieValue(Cookie[] cookies, String cookieName) {
        if(cookies == null)
            return null;
        var optionalCookie = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(cookieName))
                .findFirst();
        if (optionalCookie.isEmpty())
            return null;
        return optionalCookie.get().getValue();
    }

    public String getJwtFromRequestHeader(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader(headerString);
        if (authorizationHeader == null)
            return null;
        else if (authorizationHeader.startsWith(tokenPrefix))
            return "Bearer " + authorizationHeader.replace(tokenPrefix, "");
        return null;
    }
}
