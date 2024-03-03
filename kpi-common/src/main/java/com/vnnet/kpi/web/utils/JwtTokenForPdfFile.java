package com.vnnet.kpi.web.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class JwtTokenForPdfFile implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String USERNAME = Claims.SUBJECT;
    private static final String CREATED = "created";
    private static final String AUTHORITIES = "authorities";
    private static final String SECRET = "#cUcthUe@HP";
    private static final long EXPIRE_TIME = 15 * 24 * 60 * 60 * 1000;

    public static String generateToken(Long messHdId, String calledNum, String pdfFile) {
        Map<String, Object> claims = new HashMap<>(5);
//        claims.put(USERNAME, "");
//        claims.put(CREATED, new Date());
        claims.put("messHdId", messHdId);
        claims.put("calledNumber", calledNum);
        claims.put("pdfFile", pdfFile);
        return generateToken(claims);
    }

    public static String generateToken(Long messHdId, String calledNum, String pdfFile, String otp) {
        Map<String, Object> claims = new HashMap<>(5);
        claims.put("messHdId", messHdId);
        claims.put("calledNumber", calledNum);
        claims.put("pdfFile", pdfFile);
        claims.put("otp", otp);
        return generateToken(claims);
    }

    private static String generateToken(Map<String, Object> claims) {
        Date expirationDate = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        return Jwts.builder().setClaims(claims).setExpiration(expirationDate).signWith(SignatureAlgorithm.HS512, SECRET).compact();
    }

    public static String getUsernameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    public static Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    public static Boolean validateToken(String token, String username) {
        String userName = getUsernameFromToken(token);
        return (userName.equals(username) && !isTokenExpired(token));
    }

    public static String refreshToken(String token) {
        String refreshedToken;
        try {
            Claims claims = getClaimsFromToken(token);
            claims.put(CREATED, new Date());
            refreshedToken = generateToken(claims);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    public static Boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            System.out.println("dsds 1 " + expiration);
            System.out.println("dsds 2 " + new Date());
            return expiration.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public static String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String tokenHead = "Bearer ";
        if (token == null) {
            token = request.getHeader("token");
        } else if (token.contains(tokenHead)) {
            token = token.substring(tokenHead.length());
        }
        if ("".equals(token)) {
            token = null;
        }
        return token;
    }

}
