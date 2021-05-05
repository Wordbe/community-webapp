package kr.niceto.meetme.config.security.oauth2login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import kr.niceto.meetme.config.security.jwt.JwtUtil;
import kr.niceto.meetme.domain.common.CommonResponse;
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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        LocalDateTime issuedAt = LocalDateTime.now();
        String jwt = createJwt(authentication, issuedAt);

        setResponseHeader(response, issuedAt, jwt);
        setResponseBody(response);
    }

    private void setResponseHeader(HttpServletResponse response, LocalDateTime issuedAt, String jwt) {
        jwtUtil.setResponseHeader(response, issuedAt, jwt);
    }

    private void setResponseBody(HttpServletResponse response) throws IOException {
        CommonResponse successResponse = CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .code(HttpStatus.OK.name())
                .message(HttpStatus.OK.getReasonPhrase())
                .build();
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = objectWriter.writeValueAsString(successResponse);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().append(json);
    }

    private String createJwt(Authentication authentication, LocalDateTime issuedAt) {
        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();

        String email = (String) principal.getAttributes().get("email");
        List<String> roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        String provider = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

        return jwtUtil.createToken(email, provider, roles, issuedAt);
    }
}
