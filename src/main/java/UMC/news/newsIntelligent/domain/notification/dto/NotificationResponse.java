package UMC.news.newsIntelligent.domain.notification.dto;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import UMC.news.newsIntelligent.domain.notification.entity.Notification;
import UMC.news.newsIntelligent.domain.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NotificationResponse {

	@Builder
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class NotificationCursorDto {
		private List<NotificationDto> notifications;
		private String nextCursor;
		private boolean hasNext;

		// dto 변환 메서드
		public static NotificationCursorDto of(List<Notification> items, String nextCursor, boolean hasNext) {
			List<NotificationDto> dtos = items.stream()
				.map(NotificationDto::of).collect(Collectors.toList());
			return NotificationCursorDto.builder()
				.notifications(dtos)
				.nextCursor(nextCursor)
				.hasNext(hasNext)
				.build();
		}
	}

	@Builder
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class NotificationDto {
		private NotificationType type;
		private String content;
		private Boolean isChecked;
		private String createdAt;

		public static NotificationDto of(Notification notification) {

			return NotificationDto.builder()
				.content(notification.getContent())
				.type(notification.getNotificationType())
				.isChecked(notification.getIsChecked())
				.createdAt(String.valueOf(notification.getCreatedAt()))
				.build();
		}
	}
}
