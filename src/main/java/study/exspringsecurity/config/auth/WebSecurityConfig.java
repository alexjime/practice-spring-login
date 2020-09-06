package study.exspringsecurity.config.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import study.exspringsecurity.oauth2.CustomOAuth2Provider;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static study.exspringsecurity.oauth2.SocialType.*;

/* Spring Security 설정 */
/* Spring OAuth2 설정 */

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/", "/home", "/create",
                            "/oauth2/**", "/login/**","/css/**",
                            "/images/**", "/js/**", "/console/**", "/favicon.ico/**").permitAll()
                    .antMatchers("/facebook").hasAuthority(FACEBOOK.getRoleType())
                    .antMatchers("/google").hasAuthority(GOOGLE.getRoleType())
                    .antMatchers("/kakao").hasAuthority(KAKAO.getRoleType())
                    .antMatchers("/naver").hasAuthority(NAVER.getRoleType())
                    .anyRequest().authenticated()
//                .and()
//                    .formLogin()
//                        .loginPage("/login")
//                        .permitAll()
                .and()
                    .oauth2Login()
                    .defaultSuccessUrl("/hello")
                    .failureUrl("/loginFailure")
                .and()
                    .logout()
                        .logoutSuccessUrl("/home")
                        .invalidateHttpSession(true)
                .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"));
    }

    // password encoder 설정은 security 관련설정에 두는것이 좋다.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(
            OAuth2ClientProperties oAuth2ClientProperties,
            @Value("${spring.security.oauth2.client.registration.kakao.client-id}") String kakaoClientId,
            @Value("${spring.security.oauth2.client.registration.kakao.client-secret}") String kakaoClientSecret,
            @Value("${spring.security.oauth2.client.registration.naver.client-id}") String naverClientId,
            @Value("${spring.security.oauth2.client.registration.naver.client-id}") String naverClientSecret) {

        List<ClientRegistration> registrations = oAuth2ClientProperties
                .getRegistration().keySet().stream()
                .map(client -> getClientRegistration(oAuth2ClientProperties, client))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        registrations.add(CustomOAuth2Provider.KAKAO.getBuilder("kakao")
                .clientId(kakaoClientId)
                .clientSecret(kakaoClientSecret)
                .jwkSetUri("temp")
                .build());

        registrations.add(CustomOAuth2Provider.KAKAO.getBuilder("naver")
                .clientId(naverClientId)
                .clientSecret(naverClientSecret)
                .jwkSetUri("temp")
                .build());

        return new InMemoryClientRegistrationRepository(registrations);
    }


    private ClientRegistration getClientRegistration(OAuth2ClientProperties clientProperties, String client) {
        if("google".equals(client)) {
            OAuth2ClientProperties.Registration registration =
                    clientProperties.getRegistration().get("google");
            return CommonOAuth2Provider.GOOGLE.getBuilder(client)
                    .clientId(registration.getClientId())
                    .clientSecret(registration.getClientSecret())
                    .scope("email","profile")
                    .build();
        }

        if("facebook".equals(client)) {
            OAuth2ClientProperties.Registration registration =
                    clientProperties.getRegistration().get("facebook");
            return CommonOAuth2Provider.FACEBOOK.getBuilder(client)
                    .clientId(registration.getClientId())
                    .clientSecret(registration.getClientSecret())
                    .scope("profile")
                    .build();
        }
        return null;
    }
}
