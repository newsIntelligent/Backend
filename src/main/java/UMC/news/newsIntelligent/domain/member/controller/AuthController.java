package UMC.news.newsIntelligent.domain.member.controller;

import UMC.news.newsIntelligent.domain.mail.dto.EmailRequestDto;
import UMC.news.newsIntelligent.domain.mail.dto.VerifyRequestDto;
import UMC.news.newsIntelligent.domain.mail.entity.OtpCode;
import UMC.news.newsIntelligent.domain.member.dto.MemberResponseDto;
import UMC.news.newsIntelligent.domain.member.dto.TokenResponseDto;
import UMC.news.newsIntelligent.domain.member.service.AuthService;
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
    public void signupEmail(@RequestBody EmailRequestDto request) { auth.sendCode(request.email(), OtpCode.Type.SIGNUP);}

    @PostMapping("/login/email")
    public void loginEmail(@RequestBody EmailRequestDto request) { auth.sendCode(request.email(), OtpCode.Type.LOGIN); }

    /* --- 인증 코드 검증 --- */
    @PostMapping("/signup/verify")
    public MemberResponseDto signupVerify(@RequestBody VerifyRequestDto request) {
        return MemberResponseDto.from(auth.signupByCode(request.email(), request.code()));
    }
    @PostMapping("/login/verify")
    public TokenResponseDto loginVerify(@RequestBody VerifyRequestDto request) {
        return auth.loginByCode(request.email(), request.code());
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