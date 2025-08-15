package UMC.news.newsIntelligent.domain.notification.service;

import UMC.news.newsIntelligent.domain.member.repository.MemberRepository;
import UMC.news.newsIntelligent.domain.member.repository.MemberTopicRepository;
import UMC.news.newsIntelligent.domain.news.entity.latestCorrection.LatestCorrection;
import UMC.news.newsIntelligent.domain.notification.converter.NotificationConverter;
import UMC.news.newsIntelligent.domain.notification.dto.NotificationResponse;
import UMC.news.newsIntelligent.domain.notification.entity.Notification;
import UMC.news.newsIntelligent.domain.notification.entity.NotificationType;
import UMC.news.newsIntelligent.domain.notification.repository.NotificationRepository;
import UMC.news.newsIntelligent.global.apiPayload.code.error.ErrorCode;
import UMC.news.newsIntelligent.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

	private final MemberTopicRepository memberTopicRepository;
	private final MemberRepository memberRepository;
	private final NotificationRepository notificationRepository;

	@Override
	@Transactional(readOnly = true)
	public NotificationResponse.NotificationCursorResDTO getNotifications(
			Long memberId, String cursor, int size
	) {
		// 커서 디코딩
		LocalDateTime createdAt = null;
		Long id = null;
		if (cursor != null && !cursor.isBlank()) {
			try {
				String decoded = new String(Base64.getUrlDecoder().decode(cursor));
				String[] parts = decoded.split("_", 2);
				createdAt = LocalDateTime.parse(parts[0]);
				id = Long.parseLong(parts[1]);
			} catch (IllegalArgumentException | DateTimeException e) {
				throw new CustomException(ErrorCode.CURSOR_INVALID);
			}
		}

		int pageSize = (size < 1) ? 10 : Math.min(size, 20);
		Pageable pageable = PageRequest.of(0, pageSize + 1); // size+1 로 다음 페이지 유무 판단

		List<Notification> fetched = (createdAt == null)
				? notificationRepository.findByMemberIdBeforeCursor(memberId, pageable)
				: notificationRepository.findByMemberIdAfterCursor(memberId, createdAt, id, pageable);

		boolean hasNext = fetched.size() > pageSize;
		List<Notification> page = hasNext ? fetched.subList(0, pageSize) : fetched;

		String nextCursor = null;
		if (hasNext) {
			Notification pivot = fetched.get(pageSize);
			String raw = pivot.getCreatedAt().toString() + "_" + pivot.getId();
			nextCursor = Base64.getUrlEncoder().encodeToString(raw.getBytes());
		}

		return NotificationConverter.toCursorRes(page, nextCursor, hasNext);
	}

	@Override
	public void markAsRead(Long memberId, Long notificationId) {
		Notification n = notificationRepository
				.findByIdAndMemberId(notificationId, memberId)
				.orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));
		n.setChecked(true);
	}

	@Override
	public void markAllAsRead(Long memberId) {
		notificationRepository.markAllAsChecked(memberId);
	}


	@Transactional
	public int createForRun(String runKey, List<LatestCorrection> latestCorrections) {
		if (latestCorrections == null || latestCorrections.isEmpty()) return 0;

		int created = 0;

		for (LatestCorrection lc : latestCorrections) {
			Long topicId = lc.getTopic().getId();
			Long lcId    = lc.getId();

			// 구독 토픽 알림
			List<Long> subscriberIds = memberTopicRepository.findSubscribedMemberIdsByTopicId(topicId);
			if (!subscriberIds.isEmpty()) {
				created += createForMembers(subscriberIds, lc, NotificationType.SUBSCRIBED);
			}

			// 읽은 토픽 알림
			List<Long> readerIds = memberTopicRepository.findReadMemberIdsByTopicId(topicId);
			if (!readerIds.isEmpty()) {
				created += createForMembers(readerIds, lc, NotificationType.READ_TOPIC);
			}
		}
		return created;
	}

	private int createForMembers(List<Long> memberIds, LatestCorrection lc, NotificationType type) {
		int created = 0;
		for (Long memberId : memberIds) {
			boolean exists = notificationRepository
					.existsByMember_IdAndLatestCorrection_IdAndNotificationType(
							memberId, lc.getId(), type);

			if (exists) continue;

			var member = memberRepository.getReferenceById(memberId);
			Notification n = Notification.of(member, lc, type);
			notificationRepository.save(n);
			created++;
		}
		return created;
	}
}