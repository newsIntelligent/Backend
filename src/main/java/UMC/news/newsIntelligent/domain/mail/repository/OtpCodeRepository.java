package UMC.news.newsIntelligent.domain.mail.repository;

import UMC.news.newsIntelligent.domain.mail.entity.OtpCode;
import UMC.news.newsIntelligent.domain.mail.entity.OtpCode.Type;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpCodeRepository extends JpaRepository<OtpCode, OtpCode.PK> {
    Optional<OtpCode> findByTokenAndType(String token, Type type);
    Optional<OtpCode> findByEmailAndType(String email, Type type);
    void deleteByEmailAndType(String email, Type type);
}