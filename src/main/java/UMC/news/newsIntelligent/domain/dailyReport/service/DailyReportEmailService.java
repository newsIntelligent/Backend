package UMC.news.newsIntelligent.domain.dailyReport.service;

import java.util.List;

import org.springframework.stereotype.Service;

import UMC.news.newsIntelligent.domain.dailyReport.DailyReportEmailTemplate;
import UMC.news.newsIntelligent.domain.mail.service.MailService;
import UMC.news.newsIntelligent.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DailyReportEmailService {

	private final MailService mailService;

	public void send(
		Member member,
		List<DailyReportEmailTemplate.TopicCard> subscribedCards,
		List<DailyReportEmailTemplate.TopicCard> newCards
	) {

		String mainUrl       = mailService.getFrontBaseUrl();
		String logoSrc = "cid:mailLogo";

		String html = DailyReportEmailTemplate.render(
			"News Intelligent | Daily Report",
			mainUrl,
			logoSrc,
			subscribedCards,
			newCards
		);

		mailService.sendDailyReport(member.getEmail(), html);
	}


}
