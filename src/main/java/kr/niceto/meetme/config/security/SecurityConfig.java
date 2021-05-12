package kr.niceto.meetme.config.security;

import kr.niceto.meetme.config.security.formLogin.FormAuthenticationProvider;
import kr.niceto.meetme.config.security.formLogin.FormSuccessHandler;
//import kr.niceto.meetme.config.security.jwt.JwtAuthenticationFilter;
import kr.niceto.meetme.config.security.jwt.JwtAuthenticationEntryPoint;
import kr.niceto.meetme.config.security.jwt.JwtAuthenticationFilter;
import kr.niceto.meetme.config.security.jwt.JwtUtil;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final FormAuthenticationProvider formAuthenticationProvider;
    private final FormSuccessHandler formSuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final JwtUtil jwtUtil;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//            .exceptionHandling()
//                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
//        .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
            .authorizeRequests()
                .anyRequest().authenticated()
//        .and()
//            .httpBasic()
//                .disable()
//            .formLogin()
//                .loginPage("/login")
//                .loginProcessingUrl("/login_proc")
//                .successHandler(formSuccessHandler)
//                .permitAll()
//                .and()
//            .authenticationProvider(formAuthenticationProvider)
        .and()
            .oauth2Login()
                .userInfoEndpoint()
                    .userService(customOAuth2UserService)
                    .and()
                .successHandler(oAuth2SuccessHandler)
        .and()
            .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
        ;
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
            // /css/**, /images/**, /js/** 등 정적 리소스는 보안필터를 거치지 않게 한다.
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
            .antMatchers("/", "/login", "/signup",
                        "/api/v1/token/updateAccessToken");
    }
}
