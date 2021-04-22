package kr.niceto.meetme.web.dto;

import kr.niceto.meetme.domain.oauthaccount.OAuthAccount;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionOAuthAccount implements Serializable {
    private String name;
    private String email;
    private String picture;

    public SessionOAuthAccount(OAuthAccount oAuthAccount) {
        this.name = oAuthAccount.getName();
        this.email = oAuthAccount.getEmail();
        this.picture = oAuthAccount.getPicture();
    }
}
