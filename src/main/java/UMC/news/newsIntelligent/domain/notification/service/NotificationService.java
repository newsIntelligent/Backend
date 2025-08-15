package UMC.news.newsIntelligent.domain.notification.service;

import UMC.news.newsIntelligent.domain.news.entity.latestCorrection.LatestCorrection;
import UMC.news.newsIntelligent.domain.notification.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {
	 NotificationResponse.NotificationCursorResDTO getNotifications(Long memberId, String cursor, int size);
	 void markAsRead(Long memberId, Long notificationId);
	 void markAllAsRead(Long memberId);
	 int createForRun(String runKey, List<LatestCorrection> latestCorrections);
}
