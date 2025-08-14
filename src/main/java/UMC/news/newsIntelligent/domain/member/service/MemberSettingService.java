package UMC.news.newsIntelligent.domain.member.service;

import java.time.LocalTime;

import UMC.news.newsIntelligent.domain.member.dto.MemberSettingResponse;

public interface MemberSettingService {
	void setSubscribeNotification(Long memberId, Boolean enabled);
	void setReadTopicNotification(Long memberId, Boolean enabled);
	void setDailyReportSend(Long memberId, Boolean enabled);
	MemberSettingResponse getAllSettings(Long memberId);
	//void addReportTime(Long memberId, LocalTime time);
	Long addReportTimeAndReturnId(Long memberId, LocalTime time);
	void removeReportTime(Long memberId, Long timeId);
}
