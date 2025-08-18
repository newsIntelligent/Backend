package UMC.news.newsIntelligent.domain.member.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import UMC.news.newsIntelligent.domain.member.entity.MemberTopic;
import UMC.news.newsIntelligent.domain.topic.entity.Topic;

import org.springframework.data.domain.Page;
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
      AND (:keyword IS NULL OR LOWER(mt.topic.topicName) LIKE CONCAT('%', LOWER(:keyword), '%'))
      AND (:cursor IS NULL OR mt.topic.id < :cursor)
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
      AND (:cursor IS NULL OR mt.topic.id < :cursor)
    ORDER BY mt.topic.id DESC
""")
    Slice<Topic> getReadTopicsByMemberId(@Param("memberId") Long memberId, @Param("cursor") Long cursor, Pageable pageable);

    @Query("""
    SELECT mt.topic FROM MemberTopic mt
    WHERE mt.member.id = :memberId
      AND mt.isSubscribe = true
      AND (:cursor IS NULL OR mt.topic.id < :cursor)
    ORDER BY mt.topic.id DESC
""")
    Slice<Topic> getSubscriptionTopicsByMemberId(@Param("memberId") Long memberId, @Param("cursor") Long cursor, Pageable pageable);

    Optional<MemberTopic> findByMemberIdAndTopicId(Long memberId, Long topicId);

    @Query("""
        select t
          from MemberTopic mt
          join mt.topic t
         where mt.member.id = :memberId
           and mt.isSubscribe = true
         order by t.summaryTime desc
    """)
    Page<Topic> findSubscribedTopicsOrderByUpdatedDesc(@Param("memberId") Long memberId, Pageable pageable);

    // 해당 토픽을 구독한 멤버 조회
    @Query("""
        SELECT mt.member.id
        FROM MemberTopic mt
        WHERE mt.topic.id = :topicId AND mt.isSubscribe = true
    """)
    List<Long> findSubscribedMemberIdsByTopicId(Long topicId);

    // 해당 토픽을 읽은 멤버 조회
    @Query("""
        SELECT mt.member.id
        FROM MemberTopic mt
        WHERE mt.topic.id IN :topicId AND mt.isRead = true
    """)
    List<Long> findReadMemberIdsByTopicId(Long topicId);

    @Query("""
    select mt.topic.id
      from MemberTopic mt
     where mt.member.id = :memberId
       and mt.isSubscribe = true
       and mt.topic.id in :topicIds
""")
    List<Long> findSubscribedTopicIdsByMemberAndTopicIds(@Param("memberId") Long memberId,
                                                         @Param("topicIds") List<Long> topicIds);

    boolean existsByMemberIdAndTopicIdAndIsSubscribeTrue(Long memberId, Long topicId);
}
