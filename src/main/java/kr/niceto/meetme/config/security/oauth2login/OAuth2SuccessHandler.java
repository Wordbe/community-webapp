package kr.niceto.meetme.config.security.oauth2login;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
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

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println(authentication.toString());
//        DefaultOidcUser principal = (DefaultOidcUser) authentication.getPrincipal();
//        String jwt = principal.getIdToken().getTokenValue();
//        String expiresAt = principal.getIdToken().getExpiresAt().toString();
//
//        boolean isValid = !StringUtils.isBlank(jwt);
//        if (isValid) {
//            response.setStatus(HttpStatus.OK.value());
//
//            Cookie cookie = new Cookie("refreshToken", jwt);
//            cookie.setHttpOnly(true);
//            cookie.setSecure(true); // https
//            response.addCookie(cookie);
//            response.addHeader("x-access-token", jwt);
//            response.addHeader("x-token-expires-in", expiresAt);
//        } else {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
//        }


        SavedRequest savedRequest = requestCache.getRequest(request, response);
        // 이전 정보없이 로그인 했을 경우
        if (savedRequest != null) {
            String redirectUrl = savedRequest.getRedirectUrl();
            redirectStrategy.sendRedirect(request, response, redirectUrl);
        } else {
            redirectStrategy.sendRedirect(request, response, "/");
        }
    }
}
