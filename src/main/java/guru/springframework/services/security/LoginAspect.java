package guru.springframework.services.security;

import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Created by jt on 1/6/16.
 */
@Aspect
@Component
public class LoginAspect {

    private LoginFailureEventPublisher loginFailurePublisher;
    
    private LoginSuccessEventPublisher loginSuccessPublisher;

    @Autowired
    public void setPublisher(LoginFailureEventPublisher loginFailurePublisher) {
        this.loginFailurePublisher = loginFailurePublisher;
    }
    
    @Autowired
    public void setPublisher(LoginSuccessEventPublisher loginSuccessPublisher) {
        this.loginSuccessPublisher = loginSuccessPublisher;
    }

    @Pointcut("execution(* org.springframework.security.authentication.AuthenticationProvider.authenticate(..))")
    public void doAuthenticate(){

    }

    @Before("guru.springframework.services.security.LoginAspect.doAuthenticate() && args(authentication)")
    public void logBefore(Authentication authentication){

        System.out.println("This is before the Authenticate Method: authentication: " + authentication.isAuthenticated());
    }

    @AfterReturning(value = "guru.springframework.services.security.LoginAspect.doAuthenticate()",
            returning = "authentication")
    public void logAfterAuthenticate( Authentication authentication){
        System.out.println("This is after the Authenticate Method authentication: " + authentication.isAuthenticated());
        loginSuccessPublisher.publish(new LoginSuccessEvent(authentication));
    }

    @AfterThrowing("guru.springframework.services.security.LoginAspect.doAuthenticate() && args(authentication)")
    public void logAuthenicationException(Authentication authentication){
        String userDetails = (String) authentication.getPrincipal();
        System.out.println("Login failed for user: " + userDetails);

        loginFailurePublisher.publish(new LoginFailureEvent(authentication));

    }
}
