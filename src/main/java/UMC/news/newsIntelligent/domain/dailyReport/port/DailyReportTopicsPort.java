package UMC.news.newsIntelligent.domain.dailyReport.port;

import java.util.List;

import UMC.news.newsIntelligent.domain.dailyReport.DailyReportEmailTemplate;
import UMC.news.newsIntelligent.domain.member.entity.Member;

public interface DailyReportTopicsPort {
	List<DailyReportEmailTemplate.TopicCard> loadSubScribedUpdateCards(Member member);
	List<DailyReportEmailTemplate.TopicCard> loadNewestTopicCards(Member member, int limit);
}
