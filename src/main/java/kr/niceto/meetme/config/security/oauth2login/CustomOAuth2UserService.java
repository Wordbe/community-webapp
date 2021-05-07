package kr.niceto.meetme.config.security.oauth2login;

import kr.niceto.meetme.domain.oauthaccount.OAuthAccount;
import kr.niceto.meetme.domain.oauthaccount.OAuthAccountRepository;
import kr.niceto.meetme.web.dto.OAuth2AccountAttributes;
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

    /**
     * OAuth2 인증 완료 후 받아온 정보(userRequest)를 DB에 저장하거나 변경한다.
     * @param userRequest
     * @return
     * @throws OAuth2AuthenticationException
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration()
                .getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        OAuth2AccountAttributes attributes = OAuth2AccountAttributes.of(registrationId,
                userNameAttributeName,
                oAuth2User.getAttributes());

        OAuthAccount oAuthAccount = saveOrUpdate(attributes);

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(oAuthAccount.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private OAuthAccount saveOrUpdate(OAuth2AccountAttributes attributes) {
        OAuthAccount oAuthAccount = oAuthAccountRepository.findByEmailAndProvider(attributes.getEmail(), attributes.getProvider())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(attributes.toEntity());

        return oAuthAccountRepository.save(oAuthAccount);
    }
}
