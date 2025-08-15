package UMC.news.newsIntelligent.domain.news.entity.latestCorrection;

import UMC.news.newsIntelligent.domain.topic.entity.Topic;
import UMC.news.newsIntelligent.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "latest_correction")
public class LatestCorrection extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "run_key", nullable = false, length = 36)
    private String runKey;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @OneToMany(mappedBy = "latestCorrection", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LatestCorrectionItem> items = new ArrayList<>();

    public static LatestCorrection of(String runKey, Topic topic) {
        LatestCorrection lc = new LatestCorrection();
        lc.runKey = runKey;
        lc.topic = topic;
        return lc;
    }

    public void addItem(LatestCorrectionItem item) {
        this.items.add(item);
    }
}