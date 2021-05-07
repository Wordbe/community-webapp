package kr.niceto.meetme.config.common;

import kr.niceto.meetme.config.ApplicationContextProvider;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationContext;

@Builder
@Getter
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
        ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
        CommonMessageSource messageSource = applicationContext.getBean(CommonMessageSource.class);
        String message = messageSource.getMessage(messageCode, messageArgs);

        return CommonException.builder()
                .returnCode(returnCode)
                .messageCode(messageCode)
                .message(message)
                .cause(cause)
                .build();
    }
}
