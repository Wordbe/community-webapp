package kr.niceto.meetme.config.security.oauth2login;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import kr.niceto.meetme.config.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
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

        jwtUtil.setResponse(response, issuedAt, jwt);
        sendRedirect(request, response);
    }

    private String createJwt(Authentication authentication, LocalDateTime issuedAt) {
        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();

        String email = (String) principal.getAttributes().get("email");
        List<String> roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return jwtUtil.createToken(email, roles, issuedAt);
    }

    private void sendRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RequestCache requestCache = new HttpSessionRequestCache();
        RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            String redirectUrl = savedRequest.getRedirectUrl();
            redirectStrategy.sendRedirect(request, response, redirectUrl);
        } else {
            redirectStrategy.sendRedirect(request, response, "/");
        }
    }
}
