package com.learning.courseacademy.security.jwt;

import com.learning.courseacademy.security.services.UserPrincipal;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {

    public static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    @Value("${com.learning.jwtsecret}")
    private String jwtSecret;

    @Value("${com.learning.jwtexpiration}")
    private int jwtExpiration;

    public String generateJwtToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return Jwts.builder().setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + (jwtExpiration*1000)))
                .signWith(SignatureAlgorithm.HS512,jwtSecret)
                .compact();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        }
        catch (SignatureException e) {
            logger.error("Invalid JWT signature-> Message: {} ",e.getMessage());
        }
        catch (MalformedJwtException e) {
            logger.error("Malformed JWT token-> Message: {} ",e.getMessage());
        }
        catch (ExpiredJwtException e) {
            logger.error("Expired JWT token-> Message: {} ",e.getMessage());
        }
        catch (UnsupportedJwtException e) {
            logger.error("Unsupported exception-> Message: {} ",e.getMessage());
        }
        catch (IllegalArgumentException e) {
            logger.error("Illegal argument exception-> Message: {} ", e.getMessage());
        }
        return false;
    }

    public String getUserNameFromJwtToken(String jwtToken) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(jwtToken)
                .getBody()
                .getSubject();
    }
}
