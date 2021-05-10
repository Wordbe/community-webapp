package kr.niceto.meetme.web.controller;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import kr.niceto.meetme.config.common.CommonException;
import kr.niceto.meetme.config.common.CommonMessageSource;
import kr.niceto.meetme.config.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ApiControllerAdvice {

    private final CommonMessageSource messageSource;

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<CommonResponse> handleCommonException(CommonException ex) {
        log.debug(">>> Common Exception: {}", ex.getMessage(), ex);

        CommonResponse response = CommonResponse.builder()
                .status(Integer.parseInt(ex.getReturnCode()))
                .code(ex.getMessageCode())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({RuntimeException.class, Exception.class})
    public ResponseEntity<CommonResponse> handleUnhandledException(Exception ex, WebRequest request) {
        log.debug(">>> Unhandled Exception: request: {}", request, ex);

        String messageCode = "MSE0001";
        CommonResponse response = CommonResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .code(messageCode)
                .message(messageSource.getMessage(messageCode) + " | " + ex.getMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({SecurityException.class,
            MalformedJwtException.class,
            ExpiredJwtException.class,
            UnsupportedJwtException.class,
            IllegalArgumentException.class,
            SignatureException.class})
    public ResponseEntity<CommonResponse> handleJwtException(Exception ex) {
        log.debug(">>> JWT Exception: request: {}", ex.getMessage(), ex);

        CommonResponse response = CommonResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .code(HttpStatus.UNAUTHORIZED.name())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
}
