package com.vudt.login.configs.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

@Component
@Data
public class JwtProvider implements Serializable {
    private static final long serialVersionUID = -2550185165626007488L;

    public static final Long JWT_TOKEN_VALIDITY = 1800L; // 30 mins
    @Value("${jwt.secret}")
    private String secret; //secret key


    //Get token expired date
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    //Get token's claim
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    //Get all claims from token
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
    //Check if token expired
    public Boolean isTokenExpired(String token) {  // true => expired, false => not expired
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(Calendar.getInstance().getTime());
    }

    //Generate new token with username
    public String generateToken(String username, long invalidTime) {
        return doGenerateToken(username, invalidTime == 0 ? JWT_TOKEN_VALIDITY : invalidTime);
    }
    //Generate new token
    private String doGenerateToken(String subject, long timeAvail) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + timeAvail * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }
    //Get username from token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    private boolean isTokenHas5minRemain(String token){
        Date expireTime = getExpirationDateFromToken(token);
        Date beforeExpireTime5min = new Date(expireTime.getTime() - 300000); // 5 mins before expire time
        Date currentTime =  Calendar.getInstance().getTime();
        return currentTime.before(expireTime) && currentTime.after(beforeExpireTime5min);
    }
    public boolean canTokenBeRefreshed(String token) {
        return isTokenHas5minRemain(token);
    }


}
