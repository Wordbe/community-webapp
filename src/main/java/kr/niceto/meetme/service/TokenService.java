package kr.niceto.meetme.service;

import kr.niceto.meetme.config.common.CommonResponse;
import kr.niceto.meetme.config.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtUtil jwtUtil;

    public ResponseEntity updateAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtUtil.resolveRefreshToken(request);
        String accessToken = jwtUtil.resolveToken(request);
        LocalDateTime issuedAt = LocalDateTime.now();

        jwtUtil.isRefreshTokenValid(refreshToken);
        String updatedAccessToken = jwtUtil.updateToken(accessToken, issuedAt);

        jwtUtil.setResponseHeader(response, issuedAt, updatedAccessToken, refreshToken);

        return new ResponseEntity(CommonResponse.okBuild(), HttpStatus.OK);
    }
}
