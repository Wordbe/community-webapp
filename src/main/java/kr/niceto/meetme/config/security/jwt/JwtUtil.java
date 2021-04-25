package kr.niceto.meetme.config.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

public class JwtUtil {

    public static String createToken() {
        SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.ES256);

        return Jwts.builder()
                .setSubject("Jin")
                .signWith(secretKey)
                .compact();
    }
}
