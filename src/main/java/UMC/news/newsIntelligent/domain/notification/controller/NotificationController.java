package UMC.news.newsIntelligent.domain.notification.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import UMC.news.newsIntelligent.domain.notification.dto.NotificationResponse;
import UMC.news.newsIntelligent.domain.notification.service.NotificationService;
import UMC.news.newsIntelligent.global.apiPayload.CustomResponse;
import UMC.news.newsIntelligent.global.apiPayload.code.success.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
@Tag(name = "사용자 알림 관련 API")
public class NotificationController {

	private final NotificationService notificationService;

	@Operation(summary = "알림 목록 조회 API",
		description = "<p>구독한 토픽, 읽은 토픽에 대한 홈화면 알림 목록입니다."
			+ "<p>커서 페이징 처리하였습니다.")
	@GetMapping
	public CustomResponse<NotificationResponse.NotificationCursorDto> getNotifications(
		@RequestParam(required = false) String cursor,
		@RequestParam(defaultValue = "10") int size
	) {
		Long memberId = 0L;
		NotificationResponse.NotificationCursorDto body =
			notificationService.getNotifications(memberId, cursor, size);

		return CustomResponse.onSuccess(GeneralSuccessCode.OK, body);
	}

	@Operation(summary = "단건 알림 읽음 처리 API",
		description = "<p>홈 화면 알림 목록에서 하나의 알림에 대한 읽음 처리를 합니다.")
	@PatchMapping("/{notificationId}/check")
	public CustomResponse<Void> markAsRead(
		@PathVariable Long notificationId
	) {
		Long memberId = 0L;
		notificationService.markAsRead(memberId, notificationId);
		return CustomResponse.onSuccess(GeneralSuccessCode.OK, null);
	}

	@Operation(summary = "모든 알림 읽음 처리 API",
		description = "<p>홈 화면 알림 목록에서 알림 모두 읽기 처리를 할 경우 호출됩니다.")
	@PatchMapping("/check")
	public CustomResponse<Void> markAllAsRead() {
		Long memberId = 0L;
		notificationService.markAllAsRead(memberId);
		return CustomResponse.onSuccess(GeneralSuccessCode.OK, null);
	}

}
