package kr.niceto.meetme.web.dto;

import kr.niceto.meetme.domain.accounts.AccountRole;
import kr.niceto.meetme.domain.oauthaccount.OAuthAccount;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuth2AccountAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String provider;
    private String picture;

    @Builder
    public OAuth2AccountAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String provider, String picture) {
        this.name = name;
        this.email = email;
        this.provider = provider;
        this.picture = picture;
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
    }

    public static OAuth2AccountAttributes of(String registrationId,
                                             String userNameAttributeName,
                                             Map<String, Object> attributes) {
        if ("naver".equals(registrationId)) {
            return ofNaver(registrationId, "id", attributes);
        }

        if ("kakao".equals(registrationId)) {
            return ofKakao(registrationId, "id", attributes);
        }

        if ("facebook".equals(registrationId)) {
            return ofFacebook(registrationId, userNameAttributeName, attributes);
        }

        return ofGoogle(registrationId, userNameAttributeName, attributes);
    }

    private static OAuth2AccountAttributes ofFacebook(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> picture = (Map<String, Object>) attributes.get("picture");
        Map<String, Object> pictureData = (Map<String, Object>) picture.get("data");

        return OAuth2AccountAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .provider(registrationId)
                .picture((String) pictureData.get("url"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuth2AccountAttributes ofKakao(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response =  (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) response.get("profile");

        return OAuth2AccountAttributes.builder()
                .name((String) profile.get("nickname"))
                .email((String) response.get("email"))
                .picture((String) profile.get("profile_image_url"))
                .provider(registrationId)
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuth2AccountAttributes ofNaver(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuth2AccountAttributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .picture((String) response.get("profile_image"))
                .provider(registrationId)
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuth2AccountAttributes ofGoogle(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        return OAuth2AccountAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .provider(registrationId)
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public OAuthAccount toEntity() {
        return OAuthAccount.builder()
                .name(name)
                .email(email)
                .picture(picture)
                .provider(provider)
                .role(AccountRole.USER)
                .build();
    }
}
