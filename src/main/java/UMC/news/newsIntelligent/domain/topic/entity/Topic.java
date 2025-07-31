package UMC.news.newsIntelligent.domain.topic.entity;

import java.time.LocalDateTime;

import UMC.news.newsIntelligent.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Topic extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String topicName;

	@Column(name = "ai_summary", columnDefinition = "TEXT", nullable = false)
	private String aiSummary;

	@Column(nullable = false)
	private LocalDateTime summaryTime;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String imageUrl;
}
