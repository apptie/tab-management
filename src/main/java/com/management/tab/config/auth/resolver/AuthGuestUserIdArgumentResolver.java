package com.management.tab.config.auth.resolver;

import com.management.tab.config.auth.AuthStore;
import com.management.tab.domain.user.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class AuthGuestUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthStore store;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(GuestUser.class) && parameter.getParameterType()
                                                                             .equals(GuestUserId.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {
        UserId userId = store.get();

        if (userId == null || userId == UserId.EMPTY_USER_ID) {
            return GuestUserId.EMPTY_GUEST_ID;
        }

        return new GuestUserId(userId.getValue());
    }
}
