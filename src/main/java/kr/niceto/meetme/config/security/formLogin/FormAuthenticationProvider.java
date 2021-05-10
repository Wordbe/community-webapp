package kr.niceto.meetme.config.security.formLogin;

import kr.niceto.meetme.domain.accounts.AccountContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FormAuthenticationProvider implements AuthenticationProvider {
    private final CustomUserDetailsService customUserDetailsService;
    private final Argon2PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        AccountContext accountContext = (AccountContext) customUserDetailsService.loadUserByUsername(username);
        String passwordFromDb = accountContext.getAccount().getPassword();

        if (!passwordEncoder.matches(password, passwordFromDb)) {
            throw new BadCredentialsException("비밀번호가 틀립니다.");
        }

        return new UsernamePasswordAuthenticationToken(accountContext.getAccount(), password, accountContext.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
