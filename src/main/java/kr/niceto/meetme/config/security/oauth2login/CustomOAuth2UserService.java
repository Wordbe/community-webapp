package kr.niceto.meetme.config.security.oauth2login;

import kr.niceto.meetme.domain.oauthaccount.OAuthAccount;
import kr.niceto.meetme.domain.oauthaccount.OAuthAccountRepository;
import kr.niceto.meetme.web.dto.OAuthAttributes;
import kr.niceto.meetme.web.dto.SessionOAuthAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final OAuthAccountRepository oAuthAccountRepository;
    private final HttpSession httpSession;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId,
                userNameAttributeName,
                oAuth2User.getAttributes());

        OAuthAccount oAuthAccount = saveOrUpdate(attributes);

        httpSession.setAttribute("oAuthAccount", new SessionOAuthAccount(oAuthAccount));

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(oAuthAccount.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private OAuthAccount saveOrUpdate(OAuthAttributes attributes) {
        OAuthAccount oAuthAccount = oAuthAccountRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(attributes.toEntity());

        return oAuthAccountRepository.save(oAuthAccount);
    }
}
