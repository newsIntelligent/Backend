package UMC.news.newsIntelligent.domain.member.controller;

import UMC.news.newsIntelligent.domain.mail.dto.EmailRequestDto;
import UMC.news.newsIntelligent.domain.mail.dto.VerifyRequestDto;
import UMC.news.newsIntelligent.domain.mail.entity.OtpCode;
import UMC.news.newsIntelligent.domain.member.dto.MemberResponseDto;
import UMC.news.newsIntelligent.domain.member.dto.TokenResponseDto;
import UMC.news.newsIntelligent.domain.member.service.AuthService;
import UMC.news.newsIntelligent.global.apiPayload.CustomResponse;
import UMC.news.newsIntelligent.global.apiPayload.code.success.GeneralSuccessCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController("/api/members")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService auth;

    /* --- 메일 발송 --- */
    @PostMapping("/signup/email")
    public CustomResponse<?> signupEmail(@RequestBody EmailRequestDto request) {
        auth.sendCode(request.email(), OtpCode.Type.SIGNUP);
        return CustomResponse.onSuccess(GeneralSuccessCode.EMAIL_SENT);
    }

    @PostMapping("/login/email")
    public CustomResponse<?> loginEmail(@RequestBody EmailRequestDto request) {
        auth.sendCode(request.email(), OtpCode.Type.LOGIN);
        return CustomResponse.onSuccess(GeneralSuccessCode.EMAIL_SENT);
    }

    /* --- 인증 코드 검증 --- */
    @PostMapping("/signup/verify")
    public CustomResponse<MemberResponseDto>  signupVerify(@RequestBody VerifyRequestDto request) {
        MemberResponseDto responseDto = MemberResponseDto.from(auth.signupByCode(request.email(), request.code()));
        return CustomResponse.onSuccess(GeneralSuccessCode.SIGNUP_SUCCESS, responseDto);
    }
    @PostMapping("/login/verify")
    public CustomResponse<TokenResponseDto> loginVerify(@RequestBody VerifyRequestDto request) {
        TokenResponseDto responseDto =  auth.loginByCode(request.email(), request.code());
        return CustomResponse.onSuccess(GeneralSuccessCode.LOGIN_SUCCESS, responseDto);
    }

    /* --- 매직 링크 --- */
    @GetMapping("/signup/magic")
    public RedirectView signupMagic(@RequestParam String token) {
        auth.signupByToken(token);
        return new RedirectView("/signup/success");
    }
    @GetMapping("/login/magic")
    public RedirectView loginMagic(@RequestParam String token) {
        TokenResponseDto tr = auth.loginByToken(token);
        return new RedirectView("/login/magic-success#" + tr.accessToken());
    }
}