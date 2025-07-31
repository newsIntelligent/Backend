package UMC.news.newsIntelligent.domain.news;

import UMC.news.newsIntelligent.domain.topic.entity.Topic;
import UMC.news.newsIntelligent.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class News extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "topic_id", nullable = false)
	private Topic topic;

	@Column(nullable = false)
	private String title;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String newsSummary;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String newsLink;

	@Column(nullable = false)
	private String publishDate;

	@Column(nullable = false)
	private String press;

	// 주제 이미지
	@Column(columnDefinition = "TEXT", nullable = false)
	private String imageUrl;

	@Column(columnDefinition = "TEXT")
	private String imageSource;
}
