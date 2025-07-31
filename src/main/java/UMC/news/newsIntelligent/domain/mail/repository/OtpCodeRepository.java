package UMC.news.newsIntelligent.domain.mail.repository;

import UMC.news.newsIntelligent.domain.mail.entity.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpCodeRepository extends JpaRepository<OtpCode, OtpCode.PK> {
    Optional<OtpCode> findByToken(String token);
}