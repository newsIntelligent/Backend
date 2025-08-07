package UMC.news.newsIntelligent.global.config.security.jwt;

import UMC.news.newsIntelligent.global.config.properties.Constants;
import UMC.news.newsIntelligent.global.config.properties.JwtProperties;
import UMC.news.newsIntelligent.global.config.security.PrincipalUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    private Key signingKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
    }

    /* OTP 코드 로그인용 accessToken 발급 (패스워드 방식 x) */
    public String generateAccessToken(Long id, String email, String role) {
        long expMs = jwtProperties.getExpiration().getAccess();
        Date now = new Date();
        String jwtId = UUID.randomUUID().toString();

        return Jwts.builder()
                .setId(jwtId)
                .setSubject(email)
                .claim("id", id)
                .claim("role", role == null ? "ROLE_USER" : role)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expMs))
                .signWith(signingKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validate(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        Long memberId = claims.get("id", Long.class);
        String email = claims.getSubject();
        String role  = claims.get("role", String.class);
        if (role == null) role = "ROLE_USER";

        // principal user details로 변경
        PrincipalUserDetails principal =
                new PrincipalUserDetails(memberId, email, List.of(new SimpleGrantedAuthority(role)));

        return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
    }

    public static String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader(Constants.AUTH_HEADER);
        if (StringUtils.hasText(bearer) && bearer.startsWith(Constants.TOKEN_PREFIX)) {
            return bearer.substring(Constants.TOKEN_PREFIX.length());
        }
        return null;
    }

    public String getJwtId(String token) {
        return Jwts.parserBuilder().setSigningKey(signingKey()).build()
                .parseClaimsJws(token).getBody().getId();
    }

    public Date getExpiration(String token) {
        return Jwts.parserBuilder().setSigningKey(signingKey()).build()
                .parseClaimsJws(token).getBody().getExpiration();
    }
}
