package kr.niceto.meetme.domain.token;


import kr.niceto.meetme.domain.accounts.AccountRole;
import lombok.*;

import javax.persistence.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Entity
public class Token {

    @Id
    @GeneratedValue
    private Long id;

    private String tokenValue;

    private String account;

    private String provider;

    @Enumerated(EnumType.STRING)
    private AccountRole role;
}
