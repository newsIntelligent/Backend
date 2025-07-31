package UMC.news.newsIntelligent.domain.notification.repository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import UMC.news.newsIntelligent.domain.notification.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	List<Notification> findByMemberIdOrderByCreatedAtAsc(Long memberId);

	/**
	 * 초기 조회: 최신순으로 size+1 개 가져온다.
	 */
	@Query("""
		SELECT n
		FROM Notification n
		WHERE n.member.id = :memberId
		ORDER BY n.createdAt DESC, n.id DESC
	""")
	List<Notification> findByMemberIdBeforeCursor(
		@Param("memberId") Long memberId,
		@Param("limit") int limit);

	/**
	 *  커서 다음 조회:
	 *   createdAt < cursor.createdAt
	 *   (createdAt = cursor.createdAt AND id < cursor.id)
	 */
	@Query("""
		SELECT n
		FROM Notification n
		WHERE n.member.id = :memberId
			AND (n.createdAt < :createdAt
				OR (n.createdAt = :createdAt AND n.id < :id))
			ORDER BY n.createdAt DESC, n.id DESC
	""")
	List<Notification> findByMemberIdAfterCursor(
		@Param("memberId") Long memberId,
		@Param("createdAt")LocalDateTime createdAt,
		@Param("id") Long id,
		@Param("limit") int limit
	);
}