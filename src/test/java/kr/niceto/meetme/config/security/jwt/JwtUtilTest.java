package kr.niceto.meetme.config.security.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JwtUtilTest {

    @Autowired
    JwtUtil jwtUtil;

    @Test
    public void JWT_생성() {
        // given
        String username = "mrnt1622@naver.com";
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");
        LocalDateTime issuedAt = LocalDateTime.now();

        // when
        String token = jwtUtil.createToken(username, roles, issuedAt);

        // then
        assertThat(token).isNotNull();
        System.out.println(token);
    }
}