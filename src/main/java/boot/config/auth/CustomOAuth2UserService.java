package boot.config.auth;

import boot.config.auth.dto.*;
import boot.domain.user.User;
import boot.domain.user.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;


@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        // RegistrationId() : 소셜 로그인이 어디서 왔는지 구분하기 위한 용 (구글, 네이버 등)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        // 구글/네이버 등 응답해줄 인터페이스
        OAuth2Response auth2Response;

        if (registrationId.equals("naver")) {
            auth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            auth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        // 이사람이 신규인지 원래 있던 사람인지 구분해서 동작
        User user = saveOrUpdate(auth2Response);

        // 세션에 사용자 정보를 담기 (dto 감싸서)
        SessionUser sessionUser = new SessionUser(user);
        httpSession.setAttribute("user", sessionUser);

        /**
         *  1. DefaultOAuth2User
         *      OAuth2User인터페이스의 구현체, 이 객체에 사용자 정보가 저장되며, SecurityContext 에 저장되어 애플리케이션 내에서 사용자 정보에 접근 가능
         *      매개변수(Collection<> 권한, Map<String,Object> 사용자 정보, String 인증기관 발급 PK(라고 생각))
         *
         *  2. Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())
         *      권한을 만들어줘야하는데, 먼저 Collections.singleton() 으로 단일 권한을 포함하는 불변 컬랙션 생성
         *      SimpleGrantedAuthority(ROLE_???) : 스프링이 사용자 권한을 표현할 때 사용하는 클래스
         * */

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())
                ), auth2Response.getAttributes(), userNameAttributeName
        );
    }

    private User saveOrUpdate(OAuth2Response auth2Response) {
        User user = userRepository.findById(auth2Response.getProvider() + " " + auth2Response.getProviderId())
                .map(entity -> entity.update(auth2Response.getName(), auth2Response.getProfile()))
                .orElse(auth2Response.toEntity());

        return userRepository.save(user);
    }
}
