package UMC.news.newsIntelligent.domain.member.service;

import UMC.news.newsIntelligent.domain.member.dto.MemberSettingResponse;

public interface MemberSettingService {
	void setSubscribeNotification(Long memberId, Boolean enabled);
	void setReadTopicNotification(Long memberId, Boolean enabled);
	void setDailyReportSend(Long memberId, Boolean enabled);
	MemberSettingResponse getAllSettings(Long memberId);
}
