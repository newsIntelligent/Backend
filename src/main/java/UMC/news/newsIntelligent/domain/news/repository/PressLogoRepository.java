package UMC.news.newsIntelligent.domain.news.repository;

import UMC.news.newsIntelligent.domain.news.entity.PressLogo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PressLogoRepository extends JpaRepository<PressLogo, Long> {
    List<PressLogo> findByPressIn(Collection<String> presses);
}