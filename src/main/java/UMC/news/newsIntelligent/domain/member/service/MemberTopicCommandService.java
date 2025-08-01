package UMC.news.newsIntelligent.domain.member.service;

public interface MemberTopicCommandService {
	void markRead(Long memberId, Long topicId);
}
