package UMC.news.newsIntelligent.domain.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import UMC.news.newsIntelligent.domain.member.entity.MemberTopic;
import UMC.news.newsIntelligent.domain.member.repository.MemberRepository;
import UMC.news.newsIntelligent.domain.member.repository.MemberTopicRepository;
import UMC.news.newsIntelligent.domain.topic.repository.TopicRepository;
import UMC.news.newsIntelligent.global.apiPayload.code.error.GeneralErrorCode;
import UMC.news.newsIntelligent.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberTopicCommandServiceImpl implements MemberTopicCommandService {

	private final MemberTopicRepository memberTopicRepo;
	private final TopicRepository topicRepo;
	private final MemberRepository memberRepo;

	@Override
	public void markRead(Long memberId, Long topicId) {
		MemberTopic memberTopic = memberTopicRepo.findByMemberIdAndTopicId(memberId, topicId)
			.orElseThrow(() -> new CustomException(GeneralErrorCode.TOPIC_NOT_FOUND));
		memberTopic.markRead();
	}

}
