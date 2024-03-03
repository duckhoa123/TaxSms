package com.vnnet.kpi.console.utils;

import com.vnnet.kpi.console.security.JwtAuthenticatioToken;
import com.vnnet.kpi.web.utils.HttpUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;


public class MySecurityUtils {


    public static JwtAuthenticatioToken login(HttpServletRequest request, String username, String password, AuthenticationManager authenticationManager) {
        JwtAuthenticatioToken token = new JwtAuthenticatioToken(username, password);
        token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        token.setToken(com.vnnet.kpi.console.utils.JwtTokenUtils.generateToken(authentication));

        return token;
    }

    public static void checkAuthentication(HttpServletRequest request) {
        Authentication authentication = com.vnnet.kpi.console.utils.JwtTokenUtils.getAuthenticationeFromToken(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static String getUsername() {
        /*String username = null;
        Authentication authentication = getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal != null && principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            }
        }
        return username;*/
        if(RequestContextHolder.getRequestAttributes()==null)
            return "";
        String token =JwtTokenUtils.getToken(HttpUtils.getHttpServletRequest());
        return JwtTokenUtils.getUsernameFromToken(token);
    }

    public static String getUsername(Authentication authentication) {
        String username = null;
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal != null && principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            }
        }
        return username;
    }

    public static Authentication getAuthentication() {
        if (SecurityContextHolder.getContext() == null) {
            return null;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication;
    }

}
