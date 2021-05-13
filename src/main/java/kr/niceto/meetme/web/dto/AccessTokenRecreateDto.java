package kr.niceto.meetme.web.dto;

import kr.niceto.meetme.domain.accounts.AccountRole;
import kr.niceto.meetme.domain.token.Token;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter @Setter
@NoArgsConstructor
public class AccessTokenRecreateDto {

    private String account;
    private String provider;

    @Enumerated(EnumType.STRING)
    private AccountRole role;

    @Builder
    public AccessTokenRecreateDto(String account, String provider, AccountRole role) {
        this.account = account;
        this.provider = provider;
        this.role = role;
    }
}
