package kr.niceto.meetme.web.dto;

import kr.niceto.meetme.domain.accounts.Account;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter @Setter
@NoArgsConstructor
public class AccountSaveRequestDto {
    private String username;
    private String password;
    private String email;

    @Builder
    public AccountSaveRequestDto(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public Account toEntity() {
        return Account.builder()
                .username(username)
                .password(password)
                .email(email).build();
    }
}
