package UMC.news.newsIntelligent.domain.member.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import UMC.news.newsIntelligent.domain.member.dto.MemberSettingRequest;
import UMC.news.newsIntelligent.domain.member.dto.MemberSettingResponse;
import UMC.news.newsIntelligent.domain.member.service.MemberSettingServiceImpl;
import UMC.news.newsIntelligent.global.apiPayload.CustomResponse;
import UMC.news.newsIntelligent.global.apiPayload.code.success.GeneralSuccessCode;
import UMC.news.newsIntelligent.global.config.security.PrincipalUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/members/setting")
@RequiredArgsConstructor
@Tag(name = "마이페이지 알림 설정 API", description = "구독 토픽 알림, 읽은 토픽 알림, 데일리리포트 설정에 대한 api 입니다.")
public class MemberSettingController {

	private final MemberSettingServiceImpl settingService;

	@Operation(summary = "구독 토픽 변경사항 알림 설정(on/off)",
		description = "<p>구독한 토픽의 업데이트 알림을 토글 형식으로 설정합니다.")
	@PatchMapping("/subscribe-notification")
	public CustomResponse<Void> setSubscribeNotification(
		@AuthenticationPrincipal PrincipalUserDetails principal,
		@RequestBody MemberSettingRequest.ToggleRequest request
	) {
		settingService.setSubscribeNotification(principal.getMemberId(), request.getEnabled());
		return CustomResponse.onSuccess(GeneralSuccessCode.OK, null);
	}

	@Operation(summary = "읽은 토픽 변경사항 알림 설정(on/off)",
		description = "<p>읽은 토픽 변경사항 알림을 토글 형식으로 설정합니다.")
	@PatchMapping("/read-topic-notification")
	public CustomResponse<Void> setReadTopicNotification(
		@AuthenticationPrincipal PrincipalUserDetails principal,
		@RequestBody MemberSettingRequest.ToggleRequest request
	) {
		settingService.setReadTopicNotification(principal.getMemberId(), request.getEnabled());
		return CustomResponse.onSuccess(GeneralSuccessCode.OK, null);
	}

	@Operation(summary = "데일리 리포트 발신 여부 설정(on/off)",
		description = "<p>데일리 리포트를 수신 받을지, 안받을지를 토글 형식으로 설정합니다.")
	@PatchMapping("/daily-report")
	public CustomResponse<Void> setDailyReportSend(
		@AuthenticationPrincipal PrincipalUserDetails principal,
		@RequestBody MemberSettingRequest.ToggleRequest request
	) {
		settingService.setDailyReportSend(principal.getMemberId(), request.getEnabled());
		return CustomResponse.onSuccess(GeneralSuccessCode.OK, null);
	}

	@Operation(summary = "전체 알림 설정 상태 조회 api", description = "마이페이지 내의 전체 알림 설정 상태를 조회합니다.")
	@GetMapping
	public CustomResponse<MemberSettingResponse> getMemberSetting(
		@AuthenticationPrincipal PrincipalUserDetails principal
	) {
		MemberSettingResponse response = settingService.getAllSettings(principal.getMemberId());
		return CustomResponse.onSuccess(GeneralSuccessCode.OK, response);
	}
}
