package com.law.convertilovelaw.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthTokenFilter {
    @Autowired
    private JwtUtils jwtUtils;

    private String parseJwt(String header) {
        String[] listHeader = header.split(" ");

        if (listHeader[0].equals("Bearer")) {
            return listHeader[1];
        }

        return null;
    }

    public boolean cekAuthentication(String header, String username) {
        String jwt = parseJwt(header);
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
            String usernameFromToken = jwtUtils.getUserNameFromJwtToken(jwt);

            return usernameFromToken.equals(username);
        }
        return false;
    }

}