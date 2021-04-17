package kr.niceto.meetme.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class AccountDto {

    private Long id;
    private String username;
    private String password;
    private String email;
}
