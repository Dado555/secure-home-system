package rs.securehome.house.aspects;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import rs.securehome.common.dto.PrivilegeCheckRequestDTO;
import rs.securehome.house.annotation.HasPrivilege;
import rs.securehome.house.clients.PrivilegeClient;
import rs.securehome.house.exception.UnauthorizedException;
import rs.securehome.house.util.HttpRequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;

@Aspect
@Component
@Slf4j
public class HasPrivilegeAspect {

    private final PrivilegeClient privilegeClient;

    private final HttpRequestUtil httpRequestUtil;


    @Value("${jwt.header.string}")
    private String headerString;


    @Value("${jwt.token.prefix}")
    private String tokenPrefix;


    @Autowired
    public HasPrivilegeAspect(PrivilegeClient privilegeClient, HttpRequestUtil httpRequestUtil) {
        this.privilegeClient = privilegeClient;
        this.httpRequestUtil = httpRequestUtil;
    }

    private <T extends Annotation> T getAnnotation(JoinPoint joinPoint, Class<T> tClass) {
        var signature = (MethodSignature) joinPoint.getSignature();
        var method = signature.getMethod();
        return method.getAnnotation(tClass);
    }


    @Before("@annotation(rs.securehome.house.annotation.HasPrivilege)")
    public void checkPrivilege(JoinPoint joinPoint) {
        var args = joinPoint.getArgs();
        var request = (HttpServletRequest) args[0];
        var annotation = this.getAnnotation(joinPoint, HasPrivilege.class);
        var token = this.httpRequestUtil.getJwtFromRequestHeader(request);
        var cookieValue = this.httpRequestUtil.getCookieValue(request.getCookies(), "jwt-security");

        var checkPrivilegeDTO = new PrivilegeCheckRequestDTO(annotation.privilege());

        var response = this.privilegeClient.checkPrivilege(token, cookieValue, checkPrivilegeDTO);
        if (Boolean.FALSE.equals(response.getHasPrivilege()))
            throw new UnauthorizedException("You do not have access to this resource.");


    }
}
