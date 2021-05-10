package kr.niceto.meetme.web.controller;

import kr.niceto.meetme.config.common.CommonResponse;
import kr.niceto.meetme.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/token")
public class TokenController {

    private final TokenService tokenService;

    @GetMapping("/updateAccessToken")
    public ResponseEntity<CommonResponse> updateAccessToken(HttpServletRequest request, HttpServletResponse response) {
        return tokenService.updateAccessToken(request, response);
    }
}
