package kr.niceto.meetme.config.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Builder
@Getter
public class CommonResponse {

    private int status;
    private String code;
    private String message;

    public static void setOkResponse(HttpServletResponse response) throws IOException {
        CommonResponse successResponse = CommonResponse.okBuild();
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = objectWriter.writeValueAsString(successResponse);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().append(json);
    }

    public static CommonResponse okBuild() {
        return CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .code(HttpStatus.OK.name())
                .message(HttpStatus.OK.getReasonPhrase())
                .build();
    }
}
