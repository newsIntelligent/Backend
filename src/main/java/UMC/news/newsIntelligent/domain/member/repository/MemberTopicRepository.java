package UMC.news.newsIntelligent.domain.member.repository;

import java.util.Optional;

import UMC.news.newsIntelligent.domain.member.entity.MemberTopic;
import UMC.news.newsIntelligent.domain.topic.entity.Topic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberTopicRepository extends JpaRepository<MemberTopic, Long> {

    // memberTopic 에서 isRead = true 인 것 중, topicName 을 기준으로 keyword 검색 & 커서 페이지네이션 구현
    @Query("""
    SELECT mt.topic FROM MemberTopic mt
    WHERE mt.member.id = :memberId
      AND mt.isRead = true
      AND (:keyword IS NULL OR mt.topic.topicName LIKE %:keyword%)
      AND mt.topic.id < :cursor
    ORDER BY mt.topic.id DESC
""")
    Slice<Topic> searchReadTopicsByKeyword(
            @Param("memberId") Long memberId,
            @Param("keyword") String keyword,
            @Param("cursor") Long cursor,
            Pageable pageable
    );

    @Query("""
    SELECT mt.topic FROM MemberTopic mt
    WHERE mt.member.id = :memberId
      AND mt.isRead = true
      AND mt.topic.id < :cursor
    ORDER BY mt.topic.id DESC
""")
    Slice<Topic> getReadTopicsByMemberId(@Param("memberId") Long memberId, @Param("cursor") Long cursor, Pageable pageable);

    @Query("""
    SELECT mt.topic FROM MemberTopic mt
    WHERE mt.member.id = :memberId
      AND mt.isSubscribe = true
      AND mt.topic.id < :cursor
    ORDER BY mt.topic.id DESC
""")
    Slice<Topic> getSubscriptionTopicsByMemberId(@Param("memberId") Long memberId, @Param("cursor") Long cursor, Pageable pageable);

    Optional<MemberTopic> findByMemberIdAndTopicId(Long memberId, Long topicId);
}
