package UMC.news.newsIntelligent.domain.feedback.repository;

import UMC.news.newsIntelligent.domain.feedback.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
