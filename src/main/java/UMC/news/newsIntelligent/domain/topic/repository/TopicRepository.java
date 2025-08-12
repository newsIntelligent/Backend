package UMC.news.newsIntelligent.domain.topic.repository;

import UMC.news.newsIntelligent.domain.topic.entity.Topic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    // topicName 을 기준으로 keyword 검색 & 커서 페이지네이션 구현
    @Query("""
        SELECT t FROM Topic t
        WHERE (:keyword IS NULL OR LOWER(t.topicName) LIKE CONCAT('%', LOWER(:keyword), '%'))
          AND (:cursor IS NULL OR t.id < :cursor)
        ORDER BY t.id DESC
    """)
    Slice<Topic> findByKeywordAndCursor(@Param("keyword") String keyword, @Param("cursor") Long cursor, Pageable pageable);

    @Query("""
    SELECT t FROM Topic t
    WHERE (
        :cursorTime IS NULL
        OR t.summaryTime < :cursorTime
        OR (t.summaryTime = :cursorTime AND t.id < :cursorId)
    )
    ORDER BY t.summaryTime DESC, t.id DESC
""")
    Slice<Topic> findByCursorOrderBySummaryTimeDesc(
            @Param("cursorTime") java.time.LocalDateTime cursorTime,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );
}
