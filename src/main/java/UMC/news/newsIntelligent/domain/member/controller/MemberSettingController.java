package UMC.news.newsIntelligent.domain.member.controller;

import java.time.LocalTime;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import UMC.news.newsIntelligent.domain.dailyReport.repository.DailyReportRepository;
import UMC.news.newsIntelligent.domain.member.dto.MemberSettingRequest;
import UMC.news.newsIntelligent.domain.member.dto.MemberSettingResponse;
import UMC.news.newsIntelligent.domain.member.service.MemberSettingServiceImpl;
import UMC.news.newsIntelligent.global.apiPayload.CustomResponse;
import UMC.news.newsIntelligent.global.apiPayload.code.success.SuccessCode;
import UMC.news.newsIntelligent.global.config.security.PrincipalUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/members/setting")
@RequiredArgsConstructor
@Tag(name = "마이페이지 알림 설정 관련 API", description = "알림 설정과 설정 상태 조회")
public class MemberSettingController {

	private final MemberSettingServiceImpl settingService;
	private final DailyReportRepository dailyReportRepository;

	@Operation(summary = "구독 토픽 변경사항 알림 설정(on/off)",
		description = "<p>구독한 토픽의 업데이트 알림을 토글 형식으로 설정합니다.")
	@PatchMapping("/subscribe-notification")
	public CustomResponse<Void> setSubscribeNotification(
		@AuthenticationPrincipal PrincipalUserDetails principal,
		@RequestBody MemberSettingRequest.ToggleRequest request
	) {
		settingService.setSubscribeNotification(principal.getMemberId(), request.getEnabled());
		return CustomResponse.onSuccess(SuccessCode.OK, null);
	}

	@Operation(summary = "읽은 토픽 변경사항 알림 설정(on/off)",
		description = "<p>읽은 토픽 변경사항 알림을 토글 형식으로 설정합니다.")
	@PatchMapping("/read-topic-notification")
	public CustomResponse<Void> setReadTopicNotification(
		@AuthenticationPrincipal PrincipalUserDetails principal,
		@RequestBody MemberSettingRequest.ToggleRequest request
	) {
		settingService.setReadTopicNotification(principal.getMemberId(), request.getEnabled());
		return CustomResponse.onSuccess(SuccessCode.OK, null);
	}

	@Operation(summary = "데일리 리포트 발신 여부 설정(on/off)",
		description = "<p>데일리 리포트를 수신 받을지, 안받을지를 토글 형식으로 설정합니다.")
	@PatchMapping("/daily-report")
	public CustomResponse<Void> setDailyReportSend(
		@AuthenticationPrincipal PrincipalUserDetails principal,
		@RequestBody MemberSettingRequest.ToggleRequest request
	) {
		settingService.setDailyReportSend(principal.getMemberId(), request.getEnabled());
		return CustomResponse.onSuccess(SuccessCode.OK, null);
	}

	@Operation(summary = "전체 알림 설정 상태 조회 api", description = "마이페이지 내의 전체 알림 설정 상태를 조회합니다.")
	@GetMapping
	public CustomResponse<MemberSettingResponse> getMemberSetting(
		@AuthenticationPrincipal PrincipalUserDetails principal
	) {
		MemberSettingResponse response = settingService.getAllSettings(principal.getMemberId());
		return CustomResponse.onSuccess(SuccessCode.OK, response);
	}

	@Operation(summary = "데일리 리포트 수신 시간 추가 api",
		description = "<p>데일리 리포트 시간은 최대 3개까지 추가 가능합니다."
			+ "<p>아무것도 추가하지 않고 리포트 발신을 키면, default로 11:00이 기본 설정됩니다.")
	@PostMapping("/daily-report/time")
	public CustomResponse<Void> addReportTime(
		@AuthenticationPrincipal PrincipalUserDetails principal,
		@Valid @RequestBody MemberSettingRequest.ReportTimeRequest request
	) {
		LocalTime time = request.toLocalTime();
		settingService.addReportTime(principal.getMemberId(), time);
		return CustomResponse.onSuccess(SuccessCode.OK, null);
	}

	@Operation(summary = "데일리리포트 수신 시간 삭제", description = "timeId는 데일리리포트의 PK id를 의미합니다.")
	@DeleteMapping("/time/{timeId}")
	public CustomResponse<Void> removeReportTime(
		@AuthenticationPrincipal PrincipalUserDetails principal,
		@PathVariable Long timeId
	) {
		settingService.removeReportTime(principal.getMemberId(), timeId);
		return CustomResponse.onSuccess(SuccessCode.OK, null);
	}
}
