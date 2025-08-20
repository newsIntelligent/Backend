package UMC.news.newsIntelligent.domain.member.support;

import UMC.news.newsIntelligent.domain.member.repository.MemberTopicRepository;
import UMC.news.newsIntelligent.domain.topic.entity.Topic;
import org.springframework.data.domain.Slice;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 순수 유틸리티: 스프링 빈/필드 의존 없음.
 * 필요한 리포지토리는 메서드 파라미터로 주입해서 사용.
 */
public final class TopicQueryUtils {

    private TopicQueryUtils() { }

    public static Map<Long, Boolean> buildSubscribedMap(Long memberId,
                                                        Slice<Topic> slice,
                                                        MemberTopicRepository memberTopicRepository) {
        List<Long> topicIds = slice.getContent().stream()
                .map(Topic::getId)
                .toList();
        return buildSubscribedMap(memberId, topicIds, memberTopicRepository);
    }

    public static Map<Long, Boolean> buildSubscribedMap(Long memberId,
                                                        List<Long> topicIds,
                                                        MemberTopicRepository memberTopicRepository) {
        if (topicIds == null || topicIds.isEmpty()) {
            return Collections.emptyMap();
        }
        // 비로그인: 전부 false
        if (memberId == null) {
            return topicIds.stream().collect(Collectors.toMap(id -> id, id -> false));
        }

        List<Long> subscribedIds =
                memberTopicRepository.findSubscribedTopicIdsByMemberAndTopicIds(memberId, topicIds);
        Set<Long> subscribedSet = new HashSet<>(subscribedIds);

        return topicIds.stream().collect(Collectors.toMap(id -> id, subscribedSet::contains));
    }
}