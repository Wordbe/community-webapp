package kr.niceto.meetme.domain.common;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommonResponse {

    private int status;
    private String code;
    private String message;
}
