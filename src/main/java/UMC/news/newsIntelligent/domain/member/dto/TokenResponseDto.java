package UMC.news.newsIntelligent.domain.member.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record TokenResponseDto(
        String accessToken,
        Instant expiresAt
) {}
