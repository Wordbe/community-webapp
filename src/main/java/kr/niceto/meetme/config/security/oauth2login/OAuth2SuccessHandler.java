package kr.niceto.meetme.config.security.oauth2login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import kr.niceto.meetme.config.security.jwt.JwtUtil;
import kr.niceto.meetme.config.common.CommonResponse;
import kr.niceto.meetme.domain.token.TokenRepository;
import kr.niceto.meetme.service.TokenService;
import kr.niceto.meetme.web.dto.RefreshTokenSaveDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        LocalDateTime issuedAt = LocalDateTime.now();
        String accessToken = createJwt(authentication, issuedAt, true);
        String refreshToken = createJwt(authentication, issuedAt, false);

        setResponseHeader(response, issuedAt, accessToken, refreshToken);
        setResponseBody(response);
    }

    private void setResponseHeader(HttpServletResponse response, LocalDateTime issuedAt,
                                   String accessToken, String refreshToken) {
        jwtUtil.setResponseHeader(response, issuedAt, accessToken, refreshToken);
    }

    private void setResponseBody(HttpServletResponse response) throws IOException {
        CommonResponse.setOkResponse(response);
    }

    private String createJwt(Authentication authentication, LocalDateTime issuedAt, boolean isAccessToken) {
        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();

        String email = (String) principal.getAttributes().get("email");
        List<String> roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        String provider = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

        if (isAccessToken)
            return jwtUtil.createAccessToken(email, provider, roles, issuedAt);
        else {
            String refreshToken = jwtUtil.createRefreshToken(email, provider, roles, issuedAt);
            saveRefreshToken(refreshToken, email, provider);

            return refreshToken;
        }
    }

    private void saveRefreshToken(String refreshToken, String email, String provider) {
        RefreshTokenSaveDto refreshTokenSaveDto = RefreshTokenSaveDto.builder()
                .tokenValue(refreshToken)
                .account(email)
                .provider(provider)
                .build();
        tokenService.saveRefreshToken(refreshTokenSaveDto);
    }
}
