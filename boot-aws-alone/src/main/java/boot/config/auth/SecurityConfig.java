package boot.config.auth;

import boot.domain.user.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 *  extends WebSecurityConfigurerAdapter 는 deprecated
 *  이로 인해 @Bean 으로 등록하는
 *  SecurityFilterChain filterChain(HttpSecurity http) 구현 필요
 * */
public class SecurityConfig {



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // csrf disable (h2 콘솔 들어갈 때 인증 뜨면 안되서라고 책엔 적혀있음. jwt로 인증 하는 경우에도 csrf 꺼도됨.)
        http
                .csrf(auth -> auth.disable())
                .headers(headers -> headers
                        .frameOptions(frameOptionsConfig -> frameOptionsConfig.disable()));


        // 경로 설정
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**").permitAll()
                        .requestMatchers("/api/v1/**").hasRole(Role.USER.name())
                        .anyRequest().authenticated());

        http
                .logout(logout -> logout.logoutSuccessUrl("/"));


        http
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(costomOAuth2UserService)));

        return http.build();
    }

}
