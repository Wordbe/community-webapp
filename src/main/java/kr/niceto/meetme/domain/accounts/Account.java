package kr.niceto.meetme.domain.accounts;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Entity
public class Account {

    @Id @GeneratedValue
    private Long id;

    private String username;

    private String password;

    private String email;
}
