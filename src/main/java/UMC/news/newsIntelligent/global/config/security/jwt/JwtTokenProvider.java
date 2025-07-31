package UMC.news.newsIntelligent.global.config.security.jwt;

import UMC.news.newsIntelligent.global.config.properties.Constants;
import UMC.news.newsIntelligent.global.config.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.Collections;
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

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(email)
                .claim("id", id)
                .claim("role", role)
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

        String email = claims.getSubject();
        String role  = claims.get("role", String.class);
        if (role == null) role = "ROLE_USER";

        String finalRole = role;
        User principal = new User(email, "", Collections.singleton(() -> finalRole));
        return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
    }

    public static String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader(Constants.AUTH_HEADER);      // e.g. "Authorization"
        if (StringUtils.hasText(bearer) && bearer.startsWith(Constants.TOKEN_PREFIX)) {
            return bearer.substring(Constants.TOKEN_PREFIX.length()); // "Bearer "
        }
        return null;
    }

    public String getJwtId(String token) {
        return getClaims(token).getId();                    // jwtId
    }

    public Date getExpiration(String token) {
        return getClaims(token).getExpiration();
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
