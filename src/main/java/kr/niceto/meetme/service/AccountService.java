package kr.niceto.meetme.service;

import kr.niceto.meetme.domain.accounts.Account;
import kr.niceto.meetme.domain.accounts.AccountRepository;
import kr.niceto.meetme.web.dto.AccountSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;

    @Transactional
    public Long save(AccountSaveRequestDto requestDto) {
        Account account = requestDto.toEntity();
        return accountRepository.save(account).getId();
    }
}
