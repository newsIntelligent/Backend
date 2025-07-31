package UMC.news.newsIntelligent.global.config.security.jwt;

import UMC.news.newsIntelligent.global.config.properties.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwt;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String token = JwtTokenProvider.resolveToken(request);
        if (token != null && jwt.validate(token)) {
            SecurityContextHolder.getContext().setAuthentication(jwt.getAuthentication(token));
        }
        chain.doFilter(request, response);
    }
}
