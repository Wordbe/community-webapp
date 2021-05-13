package kr.niceto.meetme.config.security.jwt;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import kr.niceto.meetme.web.dto.AccessTokenRecreateDto;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.access-token-secret-key}")
    private String ACCESS_TOKEN_SECRET_KEY;

    @Value("${jwt.refresh-token-secret-key}")
    private String REFRESH_TOKEN_SECRET_KEY;

    @Value("${jwt.access-token-valid-minutes}")
    private int ACCESS_TOKEN_VALID_MINUTES;

    @Value("${jwt.access-token-header-name}")
    private String ACCESS_TOKEN_HEADER_NAME;

    @Value("${jwt.refresh-token-valid-days}")
    private int REFRESH_TOKEN_VALID_DAYS;

    private SecretKey accessTokenSecretKey;
    private SecretKey refreshTokenSecretKey;

    @PostConstruct
    protected void init() {
        byte[] accessTokenSecretKey = Decoders.BASE64.decode(ACCESS_TOKEN_SECRET_KEY);
        byte[] refreshTokenSecretKey = Decoders.BASE64.decode(REFRESH_TOKEN_SECRET_KEY);
        this.accessTokenSecretKey = Keys.hmacShaKeyFor(accessTokenSecretKey);
        this.refreshTokenSecretKey = Keys.hmacShaKeyFor(refreshTokenSecretKey);
    }

    public String createToken(String username, List<String> roles, LocalDateTime issuedAt) {
        return createAccessToken(username, null, roles, issuedAt);
    }

    public String createAccessToken(String username, String provider, List<String> roles, LocalDateTime issuedAt) {
        return createToken(username, provider, roles,
                            issuedAt, issuedAt.plusMinutes(ACCESS_TOKEN_VALID_MINUTES), accessTokenSecretKey);
    }

    public String createRefreshToken(String username, String provider, List<String> roles, LocalDateTime issuedAt) {
        return createToken(username, provider, roles,
                issuedAt, issuedAt.plusDays(REFRESH_TOKEN_VALID_DAYS), refreshTokenSecretKey);
    }

    public String createToken(String username, String provider, List<String> roles,
                              LocalDateTime issuedAt, LocalDateTime expiredAt, SecretKey secretKey) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles);
        claims.put("provider", provider);

        return Jwts.builder()
                .setHeaderParam("typ", Header.JWT_TYPE)
                .setClaims(claims)
                .setIssuedAt(localDateTime2Date(issuedAt))
                .setExpiration(localDateTime2Date(expiredAt))
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

    public String resolveRefreshToken(HttpServletRequest request) {
        List<String> cookies = Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .collect(Collectors.toList());
        String refreshToken = cookies.get(0);

        return refreshToken;
    }

    public boolean isRefreshTokenValid(String jwt, HttpServletRequest request) {
        if (StringUtils.isBlank(jwt)) {
            return false;
        }

        try {
            Jws<Claims> claimsJws = getRefreshTokenClaims(jwt);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (SecurityException e) {
            log.info("Invalid JWT signature.");
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            // TODO: DB에서 refreshToken 삭제
            request.setAttribute("exception", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
        } catch (SignatureException e) {
            log.info("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.");
        }
        return false;
    }

    public boolean isTokenValid(String jwt) {
        if (StringUtils.isBlank(jwt)) {
//            throw new IllegalArgumentException("JWT does not exist.");
            return false;
        }

        try {
            Jws<Claims> claimsJws = getClaims(jwt);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (SecurityException e) {
            log.info("Invalid JWT signature.");
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT signature is invalid.");
//            throw new SecurityException("JWT signature is invalid.");
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The token is invalid.");
//            throw new MalformedJwtException("The token is invalid");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The token has been expired.");
//            throw new ExpiredJwtException(null, null, "The token has been expired.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unsupported JWT token.");
//            throw new UnsupportedJwtException("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token compact of handler are invalid.");
//            throw new IllegalArgumentException("JWT token compact of handler are invalid.");
        } catch (SignatureException e) {
            log.info("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.");
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

    public Jws<Claims> getRefreshTokenClaims(String jwt) {
        return getClaims(jwt, refreshTokenSecretKey);
    }

    public Jws<Claims> getClaims(String jwt) {
        return getClaims(jwt, accessTokenSecretKey);
    }

    public Jws<Claims> getClaims(String jwt, SecretKey secretKey) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwt);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setResponseHeader(HttpServletResponse response, LocalDateTime issuedAt,
                                  String accessToken, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
//        cookie.setPath("/");
//        cookie.setSecure(true); // https

        response.addCookie(cookie);
        response.setStatus(HttpStatus.OK.value());
        response.addHeader(ACCESS_TOKEN_HEADER_NAME, accessToken);
        response.addHeader("X-Token-Expires-In", String.valueOf(issuedAt.plusMinutes(ACCESS_TOKEN_VALID_MINUTES)));
    }

    public String recreateAccessToken(AccessTokenRecreateDto accessTokenRecreateDto, LocalDateTime issuedAt) {
        List<String> roles = List.of(accessTokenRecreateDto.getRole().name());

        return createAccessToken(accessTokenRecreateDto.getAccount(),
                                accessTokenRecreateDto.getProvider(),
                                roles,
                                issuedAt);
    }
}
