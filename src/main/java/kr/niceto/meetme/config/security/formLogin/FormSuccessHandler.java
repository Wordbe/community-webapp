package kr.niceto.meetme.config.security.formLogin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import kr.niceto.meetme.config.common.CommonResponse;
import kr.niceto.meetme.config.security.jwt.JwtUtil;
import kr.niceto.meetme.domain.accounts.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
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
public class FormSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

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
        Account principal = (Account) authentication.getPrincipal();

        String username = principal.getUsername();
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        if (isAccessToken)
            return jwtUtil.createAccessToken(username, null, roles, issuedAt);
        else
            return jwtUtil.createRefreshToken(username, null, roles, issuedAt);
    }
}
