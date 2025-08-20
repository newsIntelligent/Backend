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

    @Query("""
        select mt
          from MemberTopic mt
          join fetch mt.topic t
         where mt.member.id = :memberId
           and mt.isRead = true
           and (:cursor is null or t.id < :cursor)
         order by t.id desc
    """)
    Slice<MemberTopic> getReadWithTopic(
            @Param("memberId") Long memberId,
            @Param("cursor") Long cursor,
            Pageable pageable
    );

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
         WHERE mt.topic.id = :topicId AND mt.isRead = true
    """)
    List<Long> findReadMemberIdsByTopicId(@Param("topicId") Long topicId);


    @Query("""
    select mt.topic.id
      from MemberTopic mt
     where mt.member.id = :memberId
       and mt.isSubscribe = true
       and mt.topic.id in :topicIds
""")
    List<Long> findSubscribedTopicIdsByMemberAndTopicIds(@Param("memberId") Long memberId,
                                                         @Param("topicIds") List<Long> topicIds);

    // 구독 목록
    @Query("""
        select mt
          from MemberTopic mt
          join fetch mt.topic t
         where mt.member.id = :memberId
           and mt.isSubscribe = true
           and (:cursor is null or t.id < :cursor)
         order by t.id desc
    """)
    Slice<MemberTopic> findSubscribedWithTopic(
            @Param("memberId") Long memberId,
            @Param("cursor") Long cursor,
            Pageable pageable
    );

    // 읽은 목록
    @Query("""
        select mt
          from MemberTopic mt
          join fetch mt.topic t
         where mt.member.id = :memberId
           and mt.isRead = true
           and (:keyword is null or lower(t.topicName) like concat('%', lower(:keyword), '%'))
           and (:cursor is null or t.id < :cursor)
         order by t.id desc
    """)
    Slice<MemberTopic> searchReadWithTopic(
            @Param("memberId") Long memberId,
            @Param("keyword") String keyword,
            @Param("cursor") Long cursor,
            Pageable pageable
    );

    boolean existsByMemberIdAndTopicIdAndIsSubscribeTrue(Long memberId, Long topicId);
}
