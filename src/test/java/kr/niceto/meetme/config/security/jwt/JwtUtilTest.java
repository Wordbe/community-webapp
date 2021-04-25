package kr.niceto.meetme.config.security.jwt;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    @Test
    public void JWT_생성() {
        // given

        // when
        String token = JwtUtil.createToken();

        // then
        assertThat(token).isNotNull();
        System.out.println(token);
    }
}