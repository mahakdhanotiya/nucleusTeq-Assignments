package com.mahak.capstone.interviewprocesstrackingsystem.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.mahak.capstone.interviewprocesstrackingsystem.entity.User;
public class CurrentUserUtil {

    public static String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

       Object principal = auth.getPrincipal();

    if (principal instanceof User user) {
        return user.getEmail();
    }

    return null;
}
}