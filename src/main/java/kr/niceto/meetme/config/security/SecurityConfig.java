package kr.niceto.meetme.config.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/signup").permitAll()
                .anyRequest().authenticated();
        ;
    }

    @Override
    public void configure(WebSecurity web) {
        // /css/**, /images/**, /js/** 등 정적 리소스는 보안필터를 거치지 않게 한다.
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
