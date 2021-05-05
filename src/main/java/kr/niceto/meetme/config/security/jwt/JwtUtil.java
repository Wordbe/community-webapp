package kr.niceto.meetme.config.security.jwt;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.token-valid-minutes}")
    private int TOKEN_VALID_MINUTES;

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.token-key-name}")
    private String TOKEN_KEY_NAME;

    private SecretKey secretKey;

    @PostConstruct
    protected void init() {
        byte[] decodedSecretKey = Decoders.BASE64.decode(SECRET_KEY);
        this.secretKey = Keys.hmacShaKeyFor(decodedSecretKey);
    }

    public String createToken(String username, List<String> roles, LocalDateTime issuedAt) {
        return createToken(username, null, roles, issuedAt);
    }

    public String createToken(String username, String provider, List<String> roles, LocalDateTime issuedAt) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles);
        if (!StringUtils.isBlank(provider)) {
            claims.put("provider", provider);
        }

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

    public String resolveToken(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        if (accessToken == null) {
            return "";
        }
        if (accessToken.startsWith("Bearer ")) {
            return accessToken.substring(7);
        }
        return "";
    }

    public boolean isTokenValid(String jwt, HttpServletResponse response) throws IOException {
        if (StringUtils.isBlank(jwt)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT does not exist.");
            return false;
        }

        try {
            Jws<Claims> claimsJws = getClaims(jwt);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (SecurityException e) {
            log.info("Invalid JWT signature.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT signature is invalid.");
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The token is invalid.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The token has been expired.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token compact of handler are invalid.");
        }
        return false;
    }

    public Authentication getAuthentication(String jwt) {
        Jws<Claims> claims = getClaims(jwt);
        List<String> roles = (List<String>) claims.getBody().get("roles");
        Collection<? extends GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User principal = new User(claims.getBody().getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, jwt, authorities);
    }

    public Jws<Claims> getClaims(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwt);
    }

    public void setResponseHeader(HttpServletResponse response, LocalDateTime issuedAt, String jwt) {
        Cookie cookie = new Cookie("refreshToken", jwt);
        cookie.setHttpOnly(true);
//        cookie.setPath("/");
//        cookie.setSecure(true); // https

        response.addCookie(cookie);
        response.setStatus(HttpStatus.OK.value());
        response.addHeader(TOKEN_KEY_NAME, jwt);
        response.addHeader("X-Token-Expires-In", String.valueOf(issuedAt.plusMinutes(TOKEN_VALID_MINUTES)));
    }
}
