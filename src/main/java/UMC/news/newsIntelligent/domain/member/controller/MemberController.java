package UMC.news.newsIntelligent.domain.member.controller;

import UMC.news.newsIntelligent.domain.member.dto.MemberInfoDto;
import UMC.news.newsIntelligent.domain.member.service.MemberInfoService;
import UMC.news.newsIntelligent.domain.mail.service.NotificationEmailChangeService;
import UMC.news.newsIntelligent.global.apiPayload.CustomResponse;
import UMC.news.newsIntelligent.global.apiPayload.code.error.GeneralErrorCode;
import UMC.news.newsIntelligent.global.apiPayload.code.success.GeneralSuccessCode;
import UMC.news.newsIntelligent.global.apiPayload.exception.CustomException;
import UMC.news.newsIntelligent.global.config.security.PrincipalUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name="회원 관련 API", description = "회원 정보 조회/수정")
public class MemberController {

    private final MemberInfoService memberInfoService;
    private final NotificationEmailChangeService notificationEmailChangeService;

    /* 회원 정보 조회 */
    @Operation(summary = "회원 정보 조회", description = "회원정보를 리스트로 반환하는 API입니다.")
    @GetMapping("/info")
    public CustomResponse<List<MemberInfoDto.MemberInfoResponse>> getInfo(@AuthenticationPrincipal PrincipalUserDetails principal) {

        MemberInfoDto.MemberInfoResponse response = memberInfoService.getInfo(principal.getMemberId());

        return CustomResponse.onSuccess(
                GeneralSuccessCode.GET_MEMBER_INFO,
                List.of(response)
        );
    }

    /* 닉네임 중복 확인 */
    @Operation(summary = "닉네임 중복 확인", description = "닉네임 변경 시 중복 확인을 하는 API입니다.")
    @GetMapping("/nickname-availability")
    public CustomResponse<MemberInfoDto.NicknameAvailabilityResponse> nicknameAvailability(
            @RequestParam String nickname) {
        return CustomResponse.onSuccess(GeneralSuccessCode.NICKNAME_VALID,  memberInfoService.checkNicknameAvailability(nickname));
    }

    /* 닉네임 변경 */
    @Operation(summary = "닉네임 변경", description = "중복되지 않은 닉네임으로 변경하는 API입니다.")
    @PatchMapping("/nickname")
    public CustomResponse<MemberInfoDto.MemberInfoResponse> updateNickname(
            @AuthenticationPrincipal PrincipalUserDetails principal,
            @RequestBody @Valid MemberInfoDto.UpdateNicknameRequest req) {

        if (principal == null) throw new CustomException(GeneralErrorCode.UNAUTHORIZED_401);

        return CustomResponse.onSuccess(GeneralSuccessCode.NICKNAME_CHANGED, memberInfoService.updateNickname(principal.getMemberId(), req.nickname()));
    }

    /* 알림 이메일 변경 - 인증 메일 발송 */
    @Operation(summary = "알림 이메일 변경 인증번호 전송", description = "알림 이메일 변경 시 사용자에게 변경된 이메일로 인증번호를 전송하는 API입니다.")
    @PostMapping("/notification-email/change")
    public CustomResponse<?> ChangeNotificationEmail(
            @AuthenticationPrincipal PrincipalUserDetails principal,
            @RequestBody @Valid MemberInfoDto.UpdateEmailRequest req) {
        if (principal == null) throw new CustomException(GeneralErrorCode.UNAUTHORIZED_401);
        notificationEmailChangeService.changeNotificationEmail(principal.getMemberId(), req);
        return CustomResponse.onSuccess(GeneralSuccessCode.EMAIL_SENT);
    }

    /* 알림 이메일 변경 - 인증 코드 검증 */
    @Operation(summary = "알림 이메일 변경 인증코드 검증", description = "알림 이메일 변경 시 전송된 6자리 코드를 검증하는 API입니다.")
    @PostMapping("/notification-email/verify")
    public CustomResponse<?> verifyChangeByCode(
            @AuthenticationPrincipal PrincipalUserDetails principal,
            @RequestBody @Valid MemberInfoDto.VerifyCodeRequest req) {
        if (principal == null) throw new CustomException(GeneralErrorCode.UNAUTHORIZED_401);
        notificationEmailChangeService.verifyByCode(principal.getMemberId(), req);
        return CustomResponse.onSuccess(GeneralSuccessCode.OTP_RIGHT);
    }

    /* 알림 이메일 변경 - 매직링크 */
    @Operation(summary = "리다이렉트용", description = "프론트엔드에서 구현 필요 X")
    @PostMapping("/notification-email/magic")
    public RedirectView verifyToken(@RequestParam String token) {
        notificationEmailChangeService.verifyByToken(token);

        return new RedirectView("/profile/notification-email/success");
    }
}
