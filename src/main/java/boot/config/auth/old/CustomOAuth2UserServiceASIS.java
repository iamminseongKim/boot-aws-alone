package boot.config.auth.old;

import boot.config.auth.old.dto.OAuthAttributes;
import boot.config.auth.dto.SessionUser;
import boot.domain.user.User;
import boot.domain.user.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserServiceASIS implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // RegistrationId() : 소셜 로그인이 어디서 왔는지 구분하기 위한 용 (구글, 네이버 등)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // UserNameAttributeName() : OAuth2 로그인 진행 시 키가 되는 필드 값. 즉 pk
        // 구글은 기본적으로 코드 지원, 네이버 카카오 등은 지원하지 않음. 구글의 기본 코드는 "sub"
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // OAuthAttributes
        // OAuth2UserService를 통해 가져온 OAuth2User의 attribute를 담은 클래스.
        // 이후 네이버 등 다른 소셜 로그인도 이 클래스를 사용
        OAuthAttributes authAttributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        // 이사람이 신규인지 원래 있던 사람인지 구분해서 동작
        User user = saveOrUpdate(authAttributes);

        // 세션에 사용자 정보를 담기 (dto 감싸서)
        httpSession.setAttribute("user", new SessionUser(user));

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                authAttributes.getAttributes(),
                authAttributes.getNameAttributeKey()
        );
    }

    private User saveOrUpdate(OAuthAttributes authAttributes) {
        User user = userRepository.findByEmail(authAttributes.getEmail())
                .map(entity -> entity.update(authAttributes.getName(), authAttributes.getPicture()))
                .orElse(authAttributes.toEntity());

        return userRepository.save(user);
    }


}
