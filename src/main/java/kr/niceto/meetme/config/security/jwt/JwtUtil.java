package kr.niceto.meetme.config.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${jwt.token-valid-minutes}")
    private int TOKEN_VALID_MINUTES;

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public String createToken(String username, List<String> roles, LocalDateTime issuedAt) {
        SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles);

        return Jwts.builder()
                .setHeaderParam("typ", Header.JWT_TYPE)
                .setClaims(claims)
                .setIssuedAt(localDateTime2Date(issuedAt))
                .setExpiration(localDateTime2Date(issuedAt.plusMinutes(TOKEN_VALID_MINUTES)))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private Date localDateTime2Date(LocalDateTime issuedAt) {
        return Date.from(issuedAt.atZone(ZoneId.systemDefault()).toInstant());
    }

    public void setResponse(HttpServletResponse response, LocalDateTime issuedAt, String jwt) {
        Cookie cookie = new Cookie("refreshToken", jwt);
        cookie.setHttpOnly(true);
//        cookie.setPath("/");
//        cookie.setSecure(true); // https

        response.addCookie(cookie);
        response.setStatus(HttpStatus.OK.value());
        response.addHeader("X-Access-Token", jwt);
        response.addHeader("X-Token-Expires-In", String.valueOf(issuedAt.plusMinutes(TOKEN_VALID_MINUTES)));
    }
}
