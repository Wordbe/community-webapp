package kr.niceto.meetme.service;

import kr.niceto.meetme.config.common.CommonResponse;
import kr.niceto.meetme.config.security.jwt.JwtUtil;
import kr.niceto.meetme.domain.token.Token;
import kr.niceto.meetme.domain.token.TokenRepository;
import kr.niceto.meetme.web.dto.TokenRecreateDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final TokenRepository tokenRepository;
    private final ModelMapper modelMapper;

    public ResponseEntity updateAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtUtil.resolveRefreshToken(request);
        jwtUtil.isRefreshTokenValid(refreshToken, request);

        TokenRecreateDto tokenRecreateDto = getTokenRecreateDto(refreshToken);

        LocalDateTime issuedAt = LocalDateTime.now();
        String accessToken = jwtUtil.recreateAccessToken(tokenRecreateDto, issuedAt);

        jwtUtil.setResponseHeader(response, issuedAt, accessToken, refreshToken);

        return new ResponseEntity(CommonResponse.okBuild(), HttpStatus.OK);
    }

    private TokenRecreateDto getTokenRecreateDto(String refreshToken) {
        Token tokenEntity = tokenRepository.findByTokenValue(refreshToken)
                .orElseThrow(RuntimeException::new);
        return modelMapper.map(tokenEntity, TokenRecreateDto.class);
    }
}
