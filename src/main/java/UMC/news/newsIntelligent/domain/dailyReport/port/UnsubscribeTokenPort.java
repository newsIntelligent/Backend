package UMC.news.newsIntelligent.domain.dailyReport.port;

import UMC.news.newsIntelligent.domain.member.entity.Member;

public interface UnsubscribeTokenPort {
	String issueToken(Member member);
}
