package UMC.news.newsIntelligent.domain.notification.dto;

import UMC.news.newsIntelligent.domain.notification.entity.NotificationType;
import java.util.List;

public class NotificationResponse {

	public record NotificationItemResDTO(
			Long id,
			NotificationType type,
			String content,     // 토픽명
			boolean isChecked,
			String createdAt    // ISO_LOCAL
	) {}

	public record NotificationCursorResDTO(
			List<NotificationItemResDTO> notifications,
			String nextCursor,
			boolean hasNext
	) {}
}