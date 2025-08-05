package UMC.news.newsIntelligent.domain.mail.repository;

import UMC.news.newsIntelligent.domain.mail.entity.NotificationEmailChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface NotificationEmailChangeRepository extends JpaRepository<NotificationEmailChange, Long> {
    Optional<NotificationEmailChange> findByMemberId(Long memberId);
    Optional<NotificationEmailChange> findByToken(String token);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from NotificationEmailChange nec where nec.member.id = :memberId")
    void deleteByMemberId(Long memberId);     // 즉시 삭제 되도록
}
