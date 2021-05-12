package kr.niceto.meetme.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import java.util.Locale;

@Configuration
public class AppConfig {

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:/messages/message");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(Locale.KOREAN);
        messageSource.setConcurrentRefresh(true);
        messageSource.setCacheSeconds(30);
        return messageSource;
    }

    @Bean
    public Argon2PasswordEncoder argon2PasswordEncoder() {
        return new Argon2PasswordEncoder();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
