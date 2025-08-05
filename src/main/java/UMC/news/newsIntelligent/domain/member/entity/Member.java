package UMC.news.newsIntelligent.domain.member.entity;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import UMC.news.newsIntelligent.domain.dailyReport.DailyReport;
import UMC.news.newsIntelligent.global.entity.BaseEntity;
import jakarta.persistence.*;

import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(
		uniqueConstraints = {
				@UniqueConstraint(name = "uk_member_email", columnNames = "email"),
				@UniqueConstraint(name = "uk_member_nickname", columnNames = "nickname")
		}
)
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 식별용 이메일 (로그인/회원가입)
	@Column(nullable = false, unique = true)
	private String email;

	// 알림 수신용 이메일 (기본: 가입 이메일)
	@Column(nullable = false)
	private String notificationEmail;

	// 닉네임: 가입 시 이메일 @앞부분으로 자동 설정
	@Column(nullable = false, unique = true)
	private String nickname;

	// 마지막 로그인 시각 (OTP 갱신용)
	private LocalDateTime lastLoginAt;

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
	@Builder.Default
	@OneToMany(
		mappedBy = "member",
		cascade = CascadeType.ALL,
		orphanRemoval = true,
		fetch = FetchType.LAZY)
	private List<DailyReport> dailyReports = new ArrayList<>();

	@Builder.Default
	@OneToMany(
		mappedBy = "member",
		cascade = CascadeType.ALL,
		orphanRemoval = true)
	private List<MemberTopic> memberTopics = new ArrayList<>();

	public static Member newMember(String email) {
		return Member.builder()
				.email(email)
				.subscribeTopicAlert(true)
				.readTopicAlert(true)
				.dailyReportAlert(true)
				.isDeactivated(false)
				.build();
	}

	public void updateLastLogin() {
		this.lastLoginAt = LocalDateTime.now();
	}
	public void deactivate() {
		this.isDeactivated = true;
	}
	public boolean isDeactivated() {
		return Boolean.TRUE.equals(isDeactivated);
	}
	public void changeNotificationEmail(String email) { this.notificationEmail = email; }
	public void changeNickname(String nickname) { this.nickname = nickname; }

    public void setSubscribeTopicAlert(Boolean subscribeTopicAlert) {
		this.subscribeTopicAlert = subscribeTopicAlert;
	}
	public void setReadTopicAlert(Boolean readTopicAlert) {
		this.readTopicAlert = readTopicAlert;
	}
	public void setDailyReportAlert(Boolean dailyReportAlert) {
		this.dailyReportAlert = dailyReportAlert;
	}
}
