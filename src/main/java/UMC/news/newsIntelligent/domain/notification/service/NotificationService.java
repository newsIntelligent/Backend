package UMC.news.newsIntelligent.domain.notification.service;

import UMC.news.newsIntelligent.domain.notification.dto.NotificationResponse;

public interface NotificationService {
	 NotificationResponse.NotificationCursorDto getNotifications(Long memberId, String cursor, int size);
	 void markAsRead(Long memberId, Long notificationId);
	 void markAllAsRead(Long memberId);
}
