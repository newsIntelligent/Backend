package UMC.news.newsIntelligent.domain.dailyReport.service;

import java.util.List;

import org.springframework.stereotype.Service;

import UMC.news.newsIntelligent.domain.dailyReport.DailyReportEmailTemplate;
import UMC.news.newsIntelligent.domain.dailyReport.port.DailyReportTopicsPort;
import UMC.news.newsIntelligent.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DailyReportSender {

	private final DailyReportTopicsPort topicsPort;
	private final DailyReportEmailService emailService;

	public void sendFor(Member member) {
		List<DailyReportEmailTemplate.TopicCard> subscribed = topicsPort.loadSubScribedUpdateCards(member);
		List<DailyReportEmailTemplate.TopicCard> newestTwo  = topicsPort.loadNewestTopicCards(member, 2);

		// 템플릿: 구독 섹션은 빈 리스트면 자동으로 숨김, '새로운 토픽'은 내부에서 2개 제한
		emailService.send(member, subscribed, newestTwo);
	}

}
