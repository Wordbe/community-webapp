package kr.niceto.meetme.service;

import kr.niceto.meetme.domain.accounts.AccountRepository;
import kr.niceto.meetme.web.dto.AccountSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final Argon2PasswordEncoder passwordEncoder;

    @Transactional
    public Long save(AccountSaveRequestDto requestDto) {
        requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        return accountRepository.save(requestDto.toEntity()).getId();
    }
}
