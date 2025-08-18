package UMC.news.newsIntelligent.domain.member.controller;

import UMC.news.newsIntelligent.domain.mail.dto.EmailRequestDto;
import UMC.news.newsIntelligent.domain.mail.dto.VerifyRequestDto;
import UMC.news.newsIntelligent.domain.mail.entity.OtpCode;
import UMC.news.newsIntelligent.domain.member.dto.TokenResponseDto;
import UMC.news.newsIntelligent.domain.member.entity.Member;
import UMC.news.newsIntelligent.domain.member.repository.MemberRepository;
import UMC.news.newsIntelligent.domain.member.service.AuthService;
import UMC.news.newsIntelligent.global.apiPayload.CustomResponse;
import UMC.news.newsIntelligent.global.apiPayload.code.error.ErrorCode;
import UMC.news.newsIntelligent.global.apiPayload.code.success.SuccessCode;
import UMC.news.newsIntelligent.global.apiPayload.exception.CustomException;
import UMC.news.newsIntelligent.global.config.security.PrincipalUserDetails;
import UMC.news.newsIntelligent.global.config.security.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name="사용자 및 인증 관련 API", description = "사용자 인증 및 가입/로그인/로그아웃/탈퇴")
public class AuthController {

    private final AuthService authService;
    private final MemberRepository memberRepository;

    @Value("${server.host.front}")
    private String frontHost;

    /* 메일 발송 */
    @Operation(summary = "회원가입 인증번호 전송", description = "회원가입 시 사용자에게 이메일로 인증번호를 전송하는 API입니다.")
    @PostMapping("/signup/email")
    public CustomResponse<?> signupEmail(@RequestBody EmailRequestDto request) {
        authService.sendCode(request.email(), OtpCode.Type.SIGNUP);
        return CustomResponse.onSuccess(SuccessCode.EMAIL_SENT);
    }

    @Operation(summary = "로그인 인증번호 전송", description = "로그인 시 사용자에게 이메일로 인증번호를 전송하는 API입니다.")
    @PostMapping("/login/email")
    public CustomResponse<?> loginEmail(@RequestBody EmailRequestDto request) {
        authService.sendCode(request.email(), OtpCode.Type.LOGIN);
        return CustomResponse.onSuccess(SuccessCode.EMAIL_SENT);
    }

    /* 인증 코드 검증 */
    @Operation(summary = "회원가입 인증코드 검증", description = "회원가입 시 전송된 6자리 코드를 검증하는 API입니다.")
    @PostMapping("/signup/verify")
    public CustomResponse<TokenResponseDto>  signupVerify(@RequestBody VerifyRequestDto request) {
        TokenResponseDto responseDto = authService.signupByCode(request.email(), request.code());
        return CustomResponse.onSuccess(SuccessCode.SIGNUP_SUCCESS, responseDto);
    }
    @Operation(summary = "로그인 인증코드 검증", description = "로그인 시 전송된 6자리 코드를 검증하는  API입니다.")
    @PostMapping("/login/verify")
    public CustomResponse<TokenResponseDto> loginVerify(@RequestBody VerifyRequestDto request) {
        TokenResponseDto responseDto =  authService.loginByCode(request.email(), request.code());
        return CustomResponse.onSuccess(SuccessCode.LOGIN_SUCCESS, responseDto);
    }

    /* 로그아웃 */
    @Operation(summary = "로그아웃", description = "액세스 토큰을 무효화하는 API입니다.")
    @PostMapping("/logout")
    public CustomResponse<?> logout(HttpServletRequest request) {
        String token = JwtTokenProvider.resolveToken(request);
        authService.logout(token);
        return CustomResponse.onSuccess(SuccessCode.LOGOUT_SUCCESS);
    }

    /* 회원 탈퇴 */
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 후 액세스 토큰을 무효화하는 API입니다.")
    @DeleteMapping("/withdraw")
    public CustomResponse<?> withdraw(HttpServletRequest request,
                                      @AuthenticationPrincipal PrincipalUserDetails principal) {
        String token = JwtTokenProvider.resolveToken(request);
        Member member = memberRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        authService.withdraw(member, token);
        return CustomResponse.onSuccess(SuccessCode.WITHDRAW_SUCCESS);
    }

    /* 리다이렉트 처리용 */
    private RedirectView redirectWithFragment(String path, TokenResponseDto tr) {
        String token = URLEncoder.encode(tr.accessToken(), StandardCharsets.UTF_8);
        String exp   = URLEncoder.encode(tr.expiresAt(),   StandardCharsets.UTF_8);

        String url = frontHost + path + "#token=" + token + "&exp=" + exp;

        RedirectView rv = new RedirectView(url);
        rv.setExposeModelAttributes(false);
        rv.setContextRelative(false);
        return rv;
    }

    /* 매직 링크 */
    @Operation(summary = "리다이렉트용", description = "프론트엔드에서 구현 필요 X")
    @GetMapping("/signup/magic")
    public RedirectView signupMagic(@RequestParam String token) {
        TokenResponseDto tr = authService.signupByToken(token);
        return redirectWithFragment("/signup/magic-success", tr);
    }
    @Operation(summary = "리다이렉트용", description = "프론트엔드에서 구현 필요 X")
    @GetMapping("/login/magic")
    public RedirectView loginMagic(@RequestParam String token) {
        TokenResponseDto tr = authService.loginByToken(token);
        return redirectWithFragment("/login/magic-success", tr);
    }
}