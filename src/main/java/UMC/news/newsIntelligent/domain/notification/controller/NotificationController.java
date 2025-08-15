package UMC.news.newsIntelligent.domain.notification.controller;

import UMC.news.newsIntelligent.domain.news.entity.latestCorrection.LatestCorrection;
import UMC.news.newsIntelligent.domain.news.service.LatestCorrectionService;
import UMC.news.newsIntelligent.global.config.security.PrincipalUserDetails;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import UMC.news.newsIntelligent.domain.notification.dto.NotificationResponse;
import UMC.news.newsIntelligent.domain.notification.service.NotificationService;
import UMC.news.newsIntelligent.global.apiPayload.CustomResponse;
import UMC.news.newsIntelligent.global.apiPayload.code.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
@Tag(name = "사용자 알림 관련 API", description = "알림 조회 및 읽음 처리")
public class NotificationController {

	private final NotificationService notificationService;
	private final LatestCorrectionService latestCorrectionService;

	@Operation(summary = "알림 목록 조회",
		description = "<p>구독한 토픽, 읽은 토픽에 대한 홈화면 알림 목록을 조회하는 API입니다."
			+ "<p>커서 페이징 처리하였습니다.")
	@GetMapping
	public CustomResponse<NotificationResponse.NotificationCursorResDTO> getNotifications(
		@AuthenticationPrincipal PrincipalUserDetails principal,
		@RequestParam(required = false) String cursor,
		@RequestParam(defaultValue = "10") int size
	) {
		Long memberId = principal.getMemberId();
		NotificationResponse.NotificationCursorResDTO body =
			notificationService.getNotifications(memberId, cursor, size);

		return CustomResponse.onSuccess(SuccessCode.OK, body);
	}

	@Operation(summary = "단건 알림 읽음 처리",
		description = "홈 화면 알림 목록에서 하나의 알림에 대한 읽음 처리를 하는 API입니다.")
	@PatchMapping("/{notificationId}/check")
	public CustomResponse<Void> markAsRead(
		@AuthenticationPrincipal PrincipalUserDetails principal,
		@PathVariable Long notificationId
	) {
		Long memberId = principal.getMemberId();
		notificationService.markAsRead(memberId, notificationId);
		return CustomResponse.onSuccess(SuccessCode.OK, null);
	}

	@Operation(summary = "모든 알림 읽음 처리",
		description = "홈 화면 알림 목록에서 알림 모두 읽기 처리를 할 경우 호출하는 API입니다.")
	@PatchMapping("/check")
	public CustomResponse<Void> markAllAsRead(
		@AuthenticationPrincipal PrincipalUserDetails principal
	) {
		Long memberId = principal.getMemberId();
		notificationService.markAllAsRead(memberId);
		return CustomResponse.onSuccess(SuccessCode.OK, null);
	}

	@PostMapping("/ping")
	@PermitAll
	@SecurityRequirements(value = {})
	@Operation(
			summary = "데이터 수집 파이프라인 Ping",
			description = "파이썬에서 사이클 완료 신호를 보낼 때 사용하는 엔드포인트입니다. **파이썬 전용 api이므로 절대 호출하시면 안됩니다!!**"
	)
	public CustomResponse<Void> ping() {
		String runKey = java.util.UUID.randomUUID().toString();
		List<LatestCorrection> latestCorrections = latestCorrectionService.record(runKey);
		notificationService.createForRun(runKey, latestCorrections);
		return CustomResponse.onSuccess(SuccessCode.OK, null);
	}

}
