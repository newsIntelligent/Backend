package UMC.news.newsIntelligent.global.config.security.jwt;

import UMC.news.newsIntelligent.domain.member.repository.RevokedTokenRepository;
import UMC.news.newsIntelligent.global.apiPayload.code.error.ErrorCode;
import UMC.news.newsIntelligent.global.apiPayload.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwt;
    private final RevokedTokenRepository revokedTokenRepository;
    private final HandlerExceptionResolver resolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        // preflight pass
        if (CorsUtils.isPreFlightRequest(request)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String token = JwtTokenProvider.resolveToken(request);

            if (token != null) {

                // 토큰 유효성 검사
                if (!jwt.validate(token)) {
                    log.debug("[JWT] Invalid token (validate=false). uri={}", request.getRequestURI());
                    SecurityContextHolder.clearContext();
                    resolver.resolveException(request, response, null, new CustomException(ErrorCode.INVALID_TOKEN));
                    return;
                }

                // jwtId 검사
                String jwtId = jwt.getJwtId(token);
                if (revokedTokenRepository.existsByJwtId(jwtId)) {
                    log.debug("[JWT] Revoked token jwtId={} uri={}", jwtId, request.getRequestURI());
                    SecurityContextHolder.clearContext();
                    resolver.resolveException(request, response, null, new CustomException(ErrorCode.INVALID_TOKEN));
                    return;
                }

                // 정상 토큰 → Authentication 세팅
                Authentication auth = jwt.getAuthentication(token);
                var context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(auth);
                SecurityContextHolder.setContext(context);
            }
        } catch (Exception e) {
            log.warn("[JWT] Filter error: {}", e.getMessage(), e);
            SecurityContextHolder.clearContext();
            resolver.resolveException(request, response, null, e);
        }

        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/swagger-ui/")
                || uri.startsWith("/v3/api-docs/")
                || uri.startsWith("/api/members/signup/")
                || uri.startsWith("/api/members/login/");
    }
}
