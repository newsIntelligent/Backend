package UMC.news.newsIntelligent.domain.notification.converter;

import UMC.news.newsIntelligent.domain.notification.dto.NotificationResponse;
import UMC.news.newsIntelligent.domain.notification.entity.Notification;

import java.time.format.DateTimeFormatter;
import java.util.List;

public final class NotificationConverter {
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private NotificationConverter() {}

    // 단건 매핑
    public static NotificationResponse.NotificationItemResDTO toItemDTO(Notification n) {
        String topicName = null;
        if (n.getLatestCorrection() != null &&
                n.getLatestCorrection().getTopic() != null) {
            topicName = n.getLatestCorrection().getTopic().getTopicName();
        }

        String created = (n.getCreatedAt() == null) ? null : n.getCreatedAt().format(ISO);

        return new NotificationResponse.NotificationItemResDTO(
                n.getId(),
                n.getNotificationType(),
                topicName,
                Boolean.TRUE.equals(n.getIsChecked()),
                created
        );
    }

    // 리스트 매핑
    public static List<NotificationResponse.NotificationItemResDTO> toItemDTOList(List<Notification> list) {
        return list.stream().map(NotificationConverter::toItemDTO).toList();
    }

    // 커서 매핑
    public static NotificationResponse.NotificationCursorResDTO toCursorRes(
            List<Notification> page, String nextCursor, boolean hasNext
    ) {
        return new NotificationResponse.NotificationCursorResDTO(
                toItemDTOList(page), nextCursor, hasNext
        );
    }
}