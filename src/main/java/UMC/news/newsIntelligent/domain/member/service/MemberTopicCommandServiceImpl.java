package UMC.news.newsIntelligent.domain.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import UMC.news.newsIntelligent.domain.member.entity.Member;
import UMC.news.newsIntelligent.domain.member.entity.MemberTopic;
import UMC.news.newsIntelligent.domain.member.repository.MemberRepository;
import UMC.news.newsIntelligent.domain.member.repository.MemberTopicRepository;
import UMC.news.newsIntelligent.domain.topic.entity.Topic;
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
		Member member = memberRepo.findById(memberId)
			.orElseThrow(() -> new CustomException(GeneralErrorCode.MEMBER_NOT_FOUND));
		Topic topic = topicRepo.findById(topicId)
			.orElseThrow(() -> new CustomException(GeneralErrorCode.TOPIC_NOT_FOUND));

		// 관계가 없으면 생성, 있으면 isRead 만 true 로 build
		MemberTopic memberTopic = memberTopicRepo.findByMemberIdAndTopicId(memberId, topicId)
			.orElseGet(() -> MemberTopic.builder()
				.member(member)
				.topic(topic)
				.isRead(true)
				.isSubscribe(false)
				.build());

		memberTopic.markRead();
		memberTopicRepo.save(memberTopic);
	}

	@Override
	public void subscribe(Long memberId, Long topicId) {
		Member member = memberRepo.findById(memberId)
			.orElseThrow(() -> new CustomException(GeneralErrorCode.MEMBER_NOT_FOUND));
		Topic topic = topicRepo.findById(topicId)
			.orElseThrow(() -> new CustomException(GeneralErrorCode.TOPIC_NOT_FOUND));

		// 관계가 없으면 생성, 있으면 isSubscribe 만 true 로 build
		MemberTopic memberTopic = memberTopicRepo.findByMemberIdAndTopicId(memberId, topicId)
				.orElseGet(() -> MemberTopic.builder()
					.member(member)
					.topic(topic)
					.isRead(false)
					.isSubscribe(true)
					.build());

		memberTopic.subscribe();
		memberTopicRepo.save(memberTopic);
	}

	@Override
	public void unsubscribe(Long memberId, Long topicId) {
		MemberTopic memberTopic = memberTopicRepo.findByMemberIdAndTopicId(memberId, topicId)
			.orElseThrow(() -> new CustomException(GeneralErrorCode.MEMBERTOPIC_NOT_FOUND));

		memberTopic.unsubscribe();
		memberTopicRepo.save(memberTopic);

		if(!memberTopic.getIsRead()) {
			memberTopicRepo.delete(memberTopic);
		}
	}

}
