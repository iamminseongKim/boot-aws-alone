package boot.config.auth.dto;

import boot.domain.user.User;

import java.util.Map;

public interface OAuth2Response {
    String getProvider(); // 제공자(naver, google, ...)
    String getProviderId();
    String getEmail();
    String getName();
    String getProfile();
    User toEntity();

    Map<String, Object> getAttributes();
}
