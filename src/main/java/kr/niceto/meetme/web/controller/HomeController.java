package kr.niceto.meetme.web.controller;

import kr.niceto.meetme.domain.accounts.Account;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }
}
