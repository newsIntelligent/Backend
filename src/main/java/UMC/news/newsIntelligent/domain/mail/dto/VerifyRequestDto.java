package UMC.news.newsIntelligent.domain.mail.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyRequestDto(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, max = 6) String code) {
}
