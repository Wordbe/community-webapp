package kr.niceto.meetme.service;

import kr.niceto.meetme.config.common.CommonResponse;
import kr.niceto.meetme.config.security.jwt.JwtUtil;
import kr.niceto.meetme.domain.token.Token;
import kr.niceto.meetme.domain.token.TokenRepository;
import kr.niceto.meetme.web.dto.AccessTokenRecreateDto;
import kr.niceto.meetme.web.dto.RefreshTokenSaveDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
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

        AccessTokenRecreateDto accessTokenRecreateDto = getTokenRecreateDto(refreshToken);

        LocalDateTime issuedAt = LocalDateTime.now();
        String accessToken = jwtUtil.recreateAccessToken(accessTokenRecreateDto, issuedAt);

        jwtUtil.setResponseHeader(response, issuedAt, accessToken, refreshToken);

        return new ResponseEntity(CommonResponse.okBuild(), HttpStatus.OK);
    }

    private AccessTokenRecreateDto getTokenRecreateDto(String refreshToken) {
        Token tokenEntity = tokenRepository.findByTokenValue(refreshToken)
                .orElseThrow(RuntimeException::new);
        return modelMapper.map(tokenEntity, AccessTokenRecreateDto.class);
    }

    @Transactional
    public Long saveRefreshToken(RefreshTokenSaveDto saveDto) {
        return tokenRepository.save(saveDto.toEntity()).getId();
    }
}
