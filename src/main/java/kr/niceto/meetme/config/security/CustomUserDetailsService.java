//package kr.niceto.meetme.config.security;
//
//import kr.niceto.meetme.domain.accounts.Account;
//import kr.niceto.meetme.domain.accounts.AccountRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@RequiredArgsConstructor
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//
//    private final AccountRepository accountRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Account account = accountRepository.findByUsername(username);
//        if (account == null) {
//            throw new UsernameNotFoundException("username 이 없습니다: " + username );
//        }
//
//        Set<String> userRoles = account.getUserRoles()
//                .stream()
//                .map(userRole -> userRole.getRoleName())
//                .collect(Collectors.toSet());
//
//        List<GrantedAuthority> collect = userRoles
//                .stream()
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList());
//
//        return new AccountContext(account, collect);
//    }
//}
