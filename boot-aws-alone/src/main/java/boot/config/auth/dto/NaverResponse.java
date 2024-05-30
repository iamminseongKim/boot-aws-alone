package boot.config.auth.dto;

import boot.domain.user.Role;
import boot.domain.user.User;

import java.util.Map;

public class NaverResponse implements OAuth2Response {

    private final Map<String, Object> attributes;

    public NaverResponse(Map<String, Object> attributes) {
        this.attributes = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }

    @Override
    public String getProfile() {
        return attributes.get("profile_image").toString();
    }

    @Override
    public User toEntity() {
        return User.builder()
                .id( "naver "+attributes.get("id").toString())
                .name(attributes.get("name").toString())
                .email(attributes.get("email").toString())
                .picture(attributes.get("profile_image").toString())
                .role(Role.GUEST)
                .build();
    }
}
