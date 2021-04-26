package kr.niceto.meetme.domain.oauthaccount;

import kr.niceto.meetme.domain.BaseTimeEntity;
import kr.niceto.meetme.domain.accounts.AccountRole;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class OAuthAccount extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String provider;

    @Column
    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountRole role;

    @Builder
    public OAuthAccount(String name, String email, String provider, String picture, AccountRole role) {
        this.name = name;
        this.email = email;
        this.provider = provider;
        this.picture = picture;
        this.role = role;
    }

    public OAuthAccount update(String name, String picture) {
        this.name = name;
        this.picture = picture;

        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }
}
