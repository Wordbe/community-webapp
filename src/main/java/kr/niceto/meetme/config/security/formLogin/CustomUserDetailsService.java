package kr.niceto.meetme.config.security.formLogin;

import kr.niceto.meetme.domain.accounts.Account;
import kr.niceto.meetme.domain.accounts.AccountContext;
import kr.niceto.meetme.domain.accounts.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("username 이 존재하지 않습니다: " + username));
        return new AccountContext(account);
    }
}
