package UMC.news.newsIntelligent.global.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {
    private String secretKey;
    private Expiration expiration = new Expiration();

    @Getter
    @Setter
    public static class Expiration {
        private Long access = 14400000L;
    }
}