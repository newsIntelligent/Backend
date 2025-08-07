package UMC.news.newsIntelligent.domain.mail.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailRequestDto(
        @NotBlank @Email String email) {
}
