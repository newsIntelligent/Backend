package UMC.news.newsIntelligent.domain.mail.service;

import UMC.news.newsIntelligent.domain.mail.entity.NotificationEmailChange;
import UMC.news.newsIntelligent.domain.mail.repository.NotificationEmailChangeRepository;
import UMC.news.newsIntelligent.domain.member.dto.MemberInfoDto;
import UMC.news.newsIntelligent.domain.member.entity.Member;
import UMC.news.newsIntelligent.domain.member.repository.MemberRepository;
import UMC.news.newsIntelligent.global.apiPayload.code.error.GeneralErrorCode;
import UMC.news.newsIntelligent.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationEmailChangeService {

    private final MemberRepository memberRepository;
    private final NotificationEmailChangeRepository changeRepository;
    private final MailService mailService;

    @Transactional
    public void changeNotificationEmail(Long memberId, MemberInfoDto.UpdateEmailRequest request) {
        Member member = findActive(memberId);
        String newEmail = request.newEmail().trim();

        if (newEmail.equalsIgnoreCase(member.getNotificationEmail()))
            throw new CustomException(GeneralErrorCode.BAD_REQUEST_400);

        int ttlMinutes = 10;

        String code  = mailService.generateCode();
        String token = mailService.generateToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(ttlMinutes);

        // 기존 레코드가 있으면 -> 갱신. 없으면 -> 생성
        changeRepository.findByMemberId(memberId).ifPresentOrElse(
                change -> change.refresh(newEmail, code, token, expiresAt),
                () -> changeRepository.save(
                        NotificationEmailChange.builder()
                                .member(member)
                                .newEmail(newEmail)
                                .code(code)
                                .token(token)
                                .expiresAt(expiresAt)
                                .verified(false)
                                .build()
                )
        );

        mailService.sendNotificationEmailChangeMail(newEmail, code, token);
    }

    @Transactional
    public void verifyByCode(Long memberId, MemberInfoDto.VerifyCodeRequest request) {
        Member member = findActive(memberId);

        NotificationEmailChange change = changeRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CustomException(GeneralErrorCode.MEMBER_NOT_FOUND));
        change.validateUsable();

        if (!change.getCode().equals(request.code()))
            throw new CustomException(GeneralErrorCode.OTP_WRONG);

        member.changeNotificationEmail(change.getNewEmail());
        change.markVerified();
        changeRepository.deleteByMemberId(memberId);
    }

    @Transactional
    public void verifyByToken(String token) {

        NotificationEmailChange change = changeRepository.findByToken(token)
                .orElseThrow(() -> new CustomException(GeneralErrorCode.INVALID_TOKEN));

        change.validateUsable();

        Member member = change.getMember();
        if (member.isDeactivated()) {
            throw new CustomException(GeneralErrorCode.MEMBER_ALREADY_DEACTIVATED);
        }

        member.changeNotificationEmail(change.getNewEmail());

        change.markVerified();
        changeRepository.deleteByMemberId(member.getId());
    }

    private Member findActive(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(GeneralErrorCode.MEMBER_NOT_FOUND));
        if (member.isDeactivated()) throw new CustomException(GeneralErrorCode.MEMBER_ALREADY_DEACTIVATED);
        return member;
    }
}
