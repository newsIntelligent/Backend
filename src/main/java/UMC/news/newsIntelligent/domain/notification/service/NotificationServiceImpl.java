package UMC.news.newsIntelligent.domain.notification.service;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import UMC.news.newsIntelligent.domain.notification.Notification;
import UMC.news.newsIntelligent.domain.notification.dto.NotificationResponse;
import UMC.news.newsIntelligent.domain.notification.repository.NotificationRepository;
import UMC.news.newsIntelligent.global.apiPayload.code.error.GeneralErrorCode;
import UMC.news.newsIntelligent.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository notificationRepository;

	@Override
	public NotificationResponse.NotificationCursorDto getNotifications(
		Long memberId, String cursor, int size
	) {
		// 커서 디코딩
		LocalDateTime createdAt = null;
		Long id = null;
		if(cursor != null && !cursor.isBlank()) {
			try {
				String decoded = new String(Base64.getUrlDecoder().decode(cursor));
				String[] parts = decoded.split("_", 2);
				createdAt = LocalDateTime.parse(parts[0]);
				id = Long.parseLong(parts[1]);

			} catch (IllegalArgumentException | DateTimeException e) {
				throw new CustomException(GeneralErrorCode.CURSOR_INVALID);
			}
		}
		// size+1 만큼 조회
		int limit = size +1;
		List<Notification> items = (createdAt == null)
			? notificationRepository.findByMemberIdBeforeCursor(memberId, limit)
			: notificationRepository.findByMemberIdAfterCursor(memberId, createdAt, id, limit);

		boolean hasNext = items.size() > size;
		String nextCursor = null;
		if (hasNext) {
			Notification pivot = items.get(size);
			String raw = pivot.getCreatedAt().toString() + "_" + pivot.getId();
			nextCursor = Base64.getUrlEncoder().encodeToString(raw.getBytes());
		}
		List<Notification> page = hasNext
			? items.subList(0, size)
			: items;

		return NotificationResponse.NotificationCursorDto.of(page, nextCursor, hasNext);
	}

	@Override
	@Transactional
	public void markAsRead(Long memberId, Long notificationId) {
		Notification n = notificationRepository
			.findByIdAndMemberId(notificationId, memberId)
			.orElseThrow(() -> new CustomException(GeneralErrorCode.NOTIFICATION_NOT_FOUND));
		n.setChecked(true);
	}

	@Override
	@Transactional
	public void markAllAsRead(Long memberId) {
		notificationRepository.markAllAsChecked(memberId);
	}
}
