package kr.niceto.meetme.web.controller;

import kr.niceto.meetme.config.common.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/v1")
@RestController
public class ApiController {

    @GetMapping("/mypage")
    public CommonResponse posts() {

        return CommonResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .code(HttpStatus.BAD_REQUEST.name())
                .message(HttpStatus.BAD_REQUEST.getReasonPhrase() + " | 마이페이지")
                .build();
    }

    @GetMapping("/exception/auth")
    public CommonResponse exceptions(@RequestParam(value = "referrer", required = false, defaultValue = "")
                                     String referrer) {
        log.info("인증되지 않은 URL: {}", referrer);
        return CommonResponse
                .builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .code(HttpStatus.UNAUTHORIZED.name())
                .message(HttpStatus.URI_TOO_LONG.getReasonPhrase() + " | " + referrer)
                .build();
    }

}
