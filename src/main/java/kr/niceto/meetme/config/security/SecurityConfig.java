package kr.niceto.meetme.config.security;

import kr.niceto.meetme.config.security.formLogin.FormAuthenticationProvider;
import kr.niceto.meetme.config.security.formLogin.FormSuccessHandler;
import kr.niceto.meetme.config.security.oauth2login.CustomOAuth2UserService;
import kr.niceto.meetme.config.security.oauth2login.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final FormAuthenticationProvider formAuthenticationProvider;
    private final FormSuccessHandler formSuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
            .authorizeRequests()
                .antMatchers("/", "/signup").permitAll()
                .anyRequest().authenticated()
        .and()
            .httpBasic()
                .disable()
            .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login_proc")
                .successHandler(formSuccessHandler)
                .permitAll()
        .and()
            .oauth2Login()
                .loginPage("/login")
                .loginProcessingUrl("/login_proc")
                .userInfoEndpoint()
                    .userService(customOAuth2UserService)
                    .and()
                .successHandler(oAuth2SuccessHandler)
        ;
    }

    @Override
    public void configure(WebSecurity web) {
        // /css/**, /images/**, /js/** 등 정적 리소스는 보안필터를 거치지 않게 한다.
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(formAuthenticationProvider);
    }
}
