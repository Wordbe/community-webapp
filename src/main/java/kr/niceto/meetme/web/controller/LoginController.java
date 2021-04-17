package kr.niceto.meetme.web.controller;

import kr.niceto.meetme.domain.accounts.Account;
import kr.niceto.meetme.domain.accounts.AccountRepository;
import kr.niceto.meetme.web.dto.AccountDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class LoginController {

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(AccountDto accountDto) {
//        ModelMapper modelMapper = new ModelMapper();
//        Account account = modelMapper.map(accountDto, Account.class);
        return "redirect:/";
    }
}
