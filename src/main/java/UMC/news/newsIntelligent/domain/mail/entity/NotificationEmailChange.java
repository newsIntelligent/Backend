package UMC.news.newsIntelligent.domain.mail.entity;

import UMC.news.newsIntelligent.domain.member.entity.Member;
import UMC.news.newsIntelligent.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "uk_email_change_member",
                columnNames = "member_id"
        )
)
public class NotificationEmailChange extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;  // 변경을 요청한 회원

    @Column(nullable = false)
    private String newEmail;    // 변경할 새 알림 이메일 (인증할 대상)

    @Column(nullable = false)
    private String code;    // 6자리 인증 코드

    @Column(nullable = false)
    private String token;   // 매직링크용 토큰

    @Column(nullable = false)
    private LocalDateTime expiresAt;    // 만료 시각

    /** 완료 플래그:
     * otp code나 매직링크 둘 중 하나로 검증되면 true
     * → 재사용 방지 */
    @Column(nullable = false) @Builder.Default
    private Boolean verified = false;

    public boolean isExpired() { return expiresAt.isBefore(LocalDateTime.now()); }
    public void markVerified() { this.verified = true; }
}
