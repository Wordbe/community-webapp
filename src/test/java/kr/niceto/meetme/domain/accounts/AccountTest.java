package kr.niceto.meetme.domain.accounts;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountTest {

    @Test
    @DisplayName("Account 엔티티 생성")
    public void 엔티티_생성() {
        Account test = Account.builder()
                .id(1L)
                .username("test")
                .password("1111")
                .email("test@test.com")
                .role(AccountRole.USER)
                .build();

        assertThat(test.getId()).isEqualTo(1L);
        assertThat(test.getUsername()).isEqualTo("test");
        assertThat(test.getPassword()).isEqualTo("1111");
        assertThat(test.getEmail()).isEqualTo("test@test.com");
        assertThat(test.getRole().getKey()).isEqualTo(AccountRole.USER.getKey());
    }
}