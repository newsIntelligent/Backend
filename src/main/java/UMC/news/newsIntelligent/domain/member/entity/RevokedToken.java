package UMC.news.newsIntelligent.domain.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RevokedToken {

    @Id
    private String jwtId;
    private LocalDateTime expiresAt;     // accessToken 만료 시각
}
