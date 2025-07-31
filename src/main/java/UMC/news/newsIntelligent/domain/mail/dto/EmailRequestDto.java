package UMC.news.newsIntelligent.domain.mail.dto;

import jakarta.validation.constraints.Email;

public record EmailRequestDto(@Email String email) {
}
