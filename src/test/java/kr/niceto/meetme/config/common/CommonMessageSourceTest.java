package kr.niceto.meetme.config.common;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CommonMessageSourceTest {

    @Autowired
    CommonMessageSource commonMessageSource;

    @Test
    public void 메시지_받아오기() {
        //when
        String msg0001 = commonMessageSource.getMessage("MSG0001", "테스트1");
        String msg0002 = commonMessageSource.getMessage("MSG0002");

        //then
        assertThat(msg0001).isEqualTo("테스트1 정상적으로 처리되었습니다.");
        assertThat(msg0002).isEqualTo("처리중입니다.");
    }
}