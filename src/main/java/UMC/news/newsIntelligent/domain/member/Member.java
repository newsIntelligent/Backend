package UMC.news.newsIntelligent.domain.member;

import java.util.ArrayList;
import java.util.List;

import UMC.news.newsIntelligent.domain.dailyReport.DailyReport;
import UMC.news.newsIntelligent.global.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String email;

	// 사용자 알림 여부
	@Column(name = "subscribe_topic_alert", nullable = false)
	private Boolean subscribeTopicAlert;

	@Column(name = "read_topic_alert", nullable = false)
	private Boolean readTopicAlert;

	@Column(name = "daily_report_alert", nullable = false)
	private Boolean dailyReportAlert;

	// 탈퇴 여부
	@Column(name = "is_deactivated")
	private Boolean isDeactivated;

	// 데일리 리포트
	@OneToMany(
		mappedBy = "member",
		cascade = CascadeType.ALL,
		orphanRemoval = true,
		fetch = FetchType.LAZY)
	private List<DailyReport> dailyReports = new ArrayList<>();

	@OneToMany(
		mappedBy = "member",
		cascade = CascadeType.ALL,
		orphanRemoval = true)
	private List<MemberTopic> memberTopics = new ArrayList<>();
}
