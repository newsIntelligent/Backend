package UMC.news.newsIntelligent.domain.member.repository;

import UMC.news.newsIntelligent.domain.member.entity.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, String> { }
