package kr.niceto.meetme.domain.oauthAccount;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class OauthAccount {

    @Id @GeneratedValue
    private Long id;
}
