package com.vudt.login.utils;

import com.vudt.login.services.CustomUserDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    //Get current user id
    public static Long getCurrentUserId() {
        return getCurrentUser().getUser().getUserId();
    }
    //Get current username
    public static String getCurrentUsername(){
        return getCurrentUser().getUser().getUserName();
    }
    //Get current user detail
    public static CustomUserDetail getCurrentUser() {
        return (CustomUserDetail) getCurrentAuthentication().getPrincipal();
    }
    //Get current user authentication detail
    private static Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
    //Check if current user authenticated
    public static boolean isAuthenticated() {
        return getCurrentAuthentication().isAuthenticated();
    }
    //Check current user role
    public static boolean hasRole(String role) {
        return getCurrentAuthentication().getAuthorities().stream().anyMatch(r -> r.getAuthority().equals(role));
    }
}
