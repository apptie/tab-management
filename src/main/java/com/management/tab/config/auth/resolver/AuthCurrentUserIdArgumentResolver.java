package com.management.tab.config.auth.resolver;

import com.management.tab.config.auth.AuthStore;
import com.management.tab.domain.user.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class AuthCurrentUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthStore store;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class) && parameter.getParameterType()
                                                                               .equals(CurrentUserId.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        UserId userId = store.get();

        validateUserPrincipal(userId);

        return new CurrentUserId(userId.getValue());
    }

    private void validateUserPrincipal(UserId userId) {
        if (userId == null || userId == UserId.EMPTY_USER_ID) {
            throw new UnauthorizedException();
        }
    }

    public static class UnauthorizedException extends AuthenticationException {

        public UnauthorizedException() {
            super("인증이 필요한 기능입니다.");
        }
    }
}
