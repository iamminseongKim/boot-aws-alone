package boot.config.auth;

import boot.config.auth.dto.*;
import boot.domain.user.User;
import boot.domain.user.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


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

        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }

    private User saveOrUpdate(OAuth2Response auth2Response) {
        User user = userRepository.findById(auth2Response.getProvider() + " " + auth2Response.getProviderId())
                .map(entity -> entity.update(auth2Response.getName(), auth2Response.getProfile()))
                .orElse(auth2Response.toEntity());

        return userRepository.save(user);
    }
}
