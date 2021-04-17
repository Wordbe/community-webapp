package kr.niceto.meetme.web.dto;

import kr.niceto.meetme.domain.accounts.Account;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountSaveRequestDto {

    private String username;
    private String password;
    private String email;

    public Account toEntity() {
        return Account.builder()
                .username(username)
                .password(password)
                .email(email).build();
    }
}
