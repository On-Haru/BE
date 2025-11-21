package com.example.onharu.global.jwt;

import com.example.onharu.global.exception.BusinessException;
import com.example.onharu.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        String token = extractToken(request);

        if (token == null) {
            throw new BusinessException(ErrorCode.AUTH_TOKEN_REQUIRED);
        }

        if (!jwtProvider.validateToken(token)) {
            throw new BusinessException(ErrorCode.INVALID_AUTH_TOKEN);
        }

        JwtPayload payload = jwtProvider.parseToken(token);
        request.setAttribute("userId", payload.getUserId());
        request.setAttribute("role", payload.getRole());

        return true;
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
