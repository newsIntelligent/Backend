package UMC.news.newsIntelligent.domain.notification.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import UMC.news.newsIntelligent.domain.notification.entity.NotificationType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import UMC.news.newsIntelligent.domain.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	/**
	 * 초기 조회: 최신순으로 size+1 개 가져온다.
	 */
	@Query("""
        SELECT DISTINCT n
        FROM Notification n
        LEFT JOIN FETCH n.latestCorrection lc
        LEFT JOIN FETCH lc.topic t
        WHERE n.member.id = :memberId
        ORDER BY n.createdAt DESC, n.id DESC
    """)
	List<Notification> findByMemberIdBeforeCursor(
			@Param("memberId") Long memberId,
			Pageable pageable
	);

	/**
	 *  커서 다음 조회:
	 *   createdAt < cursor.createdAt
	 *   (createdAt = cursor.createdAt AND id < cursor.id)
	 */
	@Query("""
        SELECT DISTINCT n
        FROM Notification n
        LEFT JOIN FETCH n.latestCorrection lc
        LEFT JOIN FETCH lc.topic t
        WHERE n.member.id = :memberId
          AND (n.createdAt < :createdAt
               OR (n.createdAt = :createdAt AND n.id < :id))
        ORDER BY n.createdAt DESC, n.id DESC
    """)
	List<Notification> findByMemberIdAfterCursor(
			@Param("memberId") Long memberId,
			@Param("createdAt") LocalDateTime createdAt,
			@Param("id") Long id,
			Pageable pageable
	);

	Optional<Notification> findByIdAndMemberId(Long id, Long memberId);

	@Modifying
	@Query("""
        UPDATE Notification n
        SET n.isChecked = true
        WHERE n.member.id = :memberId AND n.isChecked = false
    """)
	void markAllAsChecked(@Param("memberId") Long memberId);

	// 특정 멤버가 특정 업데이트 사항을 특정 알림유형으로 받은 적이 있는지
	boolean existsByMember_IdAndLatestCorrection_IdAndNotificationType(
			Long memberId, Long latestCorrectionId, NotificationType type
	);
}