package UMC.news.newsIntelligent.domain.member.service;

public interface MemberSettingService {
	void setSubscribeNotification(Long memberId, Boolean enabled);
	void setReadTopicNotification(Long memberId, Boolean enabled);
	void setDailyReportSend(Long memberId, Boolean enabled);
}
