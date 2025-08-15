package UMC.news.newsIntelligent.domain.news.entity.latestCorrection;

import UMC.news.newsIntelligent.domain.news.entity.News;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "latest_correction_item")
public class LatestCorrectionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name="latest_correction_id", nullable=false)
    private LatestCorrection latestCorrection;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name="news_id", nullable=false)
    private News news;

    public static LatestCorrectionItem of(LatestCorrection lc, News news) {
        LatestCorrectionItem item = new LatestCorrectionItem();
        item.latestCorrection = lc;
        item.news = news;
        return item;
    }
}