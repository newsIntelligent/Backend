package UMC.news.newsIntelligent.domain.member.controller;

import UMC.news.newsIntelligent.domain.mail.dto.EmailRequestDto;
import UMC.news.newsIntelligent.domain.mail.dto.VerifyRequestDto;
import UMC.news.newsIntelligent.domain.mail.entity.OtpCode;
import UMC.news.newsIntelligent.domain.member.dto.MemberResponseDto;
import UMC.news.newsIntelligent.domain.member.dto.TokenResponseDto;
import UMC.news.newsIntelligent.domain.member.service.AuthService;
import UMC.news.newsIntelligent.global.apiPayload.CustomResponse;
import UMC.news.newsIntelligent.global.apiPayload.code.success.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Tag(name="사용자 및 인증 관련 API", description = "사용자 인증 및 가입/로그인/로그아웃/탈퇴")
public class AuthController {

    private final AuthService auth;

    /* --- 메일 발송 --- */
    @Operation(summary = "회원가입 인증번호 전송", description = "회원가입 시 사용자에게 이메일로 인증번호를 전송하는 API입니다.")
    @PostMapping("/signup/email")
    public CustomResponse<?> signupEmail(@RequestBody EmailRequestDto request) {
        auth.sendCode(request.email(), OtpCode.Type.SIGNUP);
        return CustomResponse.onSuccess(GeneralSuccessCode.EMAIL_SENT);
    }

    @Operation(summary = "로그인 인증번호 전송", description = "로그인 시 사용자에게 이메일로 인증번호를 전송하는 API입니다.")
    @PostMapping("/login/email")
    public CustomResponse<?> loginEmail(@RequestBody EmailRequestDto request) {
        auth.sendCode(request.email(), OtpCode.Type.LOGIN);
        return CustomResponse.onSuccess(GeneralSuccessCode.EMAIL_SENT);
    }

    /* --- 인증 코드 검증 --- */
    @Operation(summary = "회원가입 인증코드 검증", description = "회원가입 시 전송된 6자리 코드를 검증하는  API입니다.")
    @PostMapping("/signup/verify")
    public CustomResponse<MemberResponseDto>  signupVerify(@RequestBody VerifyRequestDto request) {
        MemberResponseDto responseDto = MemberResponseDto.from(auth.signupByCode(request.email(), request.code()));
        return CustomResponse.onSuccess(GeneralSuccessCode.SIGNUP_SUCCESS, responseDto);
    }
    @Operation(summary = "로그인 인증코드 검증", description = "로그인 시 전송된 6자리 코드를 검증하는  API입니다.")
    @PostMapping("/login/verify")
    public CustomResponse<TokenResponseDto> loginVerify(@RequestBody VerifyRequestDto request) {
        TokenResponseDto responseDto =  auth.loginByCode(request.email(), request.code());
        return CustomResponse.onSuccess(GeneralSuccessCode.LOGIN_SUCCESS, responseDto);
    }

    /* --- 매직 링크 --- */
    @Operation(summary = "리다이렉트용", description = "프론트엔드에서 구현 필요 X")
    @GetMapping("/signup/magic")
    public RedirectView signupMagic(@RequestParam String token) {
        auth.signupByToken(token);
        return new RedirectView("/signup/success");
    }
    @Operation(summary = "리다이렉트용", description = "프론트엔드에서 구현 필요 X")
    @GetMapping("/login/magic")
    public RedirectView loginMagic(@RequestParam String token) {
        TokenResponseDto tr = auth.loginByToken(token);
        return new RedirectView("/login/magic-success#" + tr.accessToken());
    }
}