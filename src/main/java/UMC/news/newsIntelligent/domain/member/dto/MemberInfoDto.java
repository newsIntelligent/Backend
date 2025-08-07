package UMC.news.newsIntelligent.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;

public class MemberInfoDto {

    @Builder
    public record MemberInfoResponse(
            Long id,
            String  email,
            String notificationEmail,
            String nickname,
            Boolean subscribe_topic_alert,
            Boolean read_topic_alert,
            Boolean daily_report_alert,
            Boolean is_deactivated,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    @Builder
    public record UpdateNicknameRequest (
            @NotBlank @Size(min = 2, max = 20) String nickname
    ) {}

    @Builder
    public record NicknameAvailabilityResponse (
            String nickname, boolean available
    ) {}

    @Builder
    public record UpdateEmailRequest (
            @NotBlank @Email String newEmail
    ) {}

    @Builder
    public record VerifyCodeRequest (
            @NotBlank @Email String newEmail,
            @NotBlank @Size(min = 6, max = 6) String code
    ) {}
}