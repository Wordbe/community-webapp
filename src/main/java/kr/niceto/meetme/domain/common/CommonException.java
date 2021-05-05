package kr.niceto.meetme.domain.common;

import lombok.Builder;

@Builder
public class CommonException extends RuntimeException {

    private String returnCode;
    private String messageCode;
    private String message;
    private Throwable cause;

    @Override
    public String getMessage() {
        return "[" + messageCode + " | " + message + "]";
    }

    public static CommonException create(Throwable cause,
                                         String returnCode,
                                         String messageCode,
                                         Object... messageArgs) {
        return CommonException.builder()
                .returnCode(returnCode)
                .messageCode(messageCode)
                .message("")
                .cause(cause)
                .build();
    }
}
