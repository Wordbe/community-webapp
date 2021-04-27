package kr.niceto.meetme.domain.oauthaccount;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, Long> {
    Optional<OAuthAccount> findByEmailAndProvider(String email, String provider);
}
