package UMC.news.newsIntelligent.domain.member.entity;

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
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member_topic")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberTopic extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "topic_id", nullable = false)
	private Topic topic;

	@Column(nullable = false)
	private Boolean isRead;

	@Column(nullable = false)
	private Boolean isSubscribe;

	/**
	 *  상태 변경 메서드
	 */
	public void markRead() {
		this.isRead = true;
	}

	public void subscribe() {
		this.isSubscribe = true;
	}

	public void unsubscribe() {
		this.isSubscribe = false;
	}
}
