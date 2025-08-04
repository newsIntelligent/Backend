package UMC.news.newsIntelligent.domain.member.dto;

import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberSettingResponse {

	private Boolean subscribeNotification;
	private Boolean readTopicNotification;
	private Boolean dailyReportSend;
	private List<LocalTime> dailyReportTimes;  // "HH:mm" 문자열 목록
}
