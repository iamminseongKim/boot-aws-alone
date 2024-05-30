package boot.config.auth.dto;

import boot.domain.user.Role;
import boot.domain.user.User;

import java.util.Map;

public class GoogleResponse implements OAuth2Response{

    private final Map<String, Object> attributes;

    public GoogleResponse(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return attributes.get("sub").toString();
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
        return attributes.get("picture").toString();
    }

    @Override
    public User toEntity() {
        return User.builder()
                .id("google " + attributes.get("sub").toString())
                .name(attributes.get("name").toString())
                .email(attributes.get("email").toString())
                .picture(attributes.get("picture").toString())
                .role(Role.GUEST)
                .build();
    }
}
