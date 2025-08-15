package UMC.news.newsIntelligent.domain.member.service;

import UMC.news.newsIntelligent.domain.member.converter.MemberTopicConverter;
import UMC.news.newsIntelligent.domain.member.dto.MemberTopicResponseDTO;
import UMC.news.newsIntelligent.domain.member.repository.MemberTopicRepository;
import UMC.news.newsIntelligent.domain.news.repository.NewsRepository;
import UMC.news.newsIntelligent.domain.news.repository.projection.OldestPerTopicProjection;
import UMC.news.newsIntelligent.domain.topic.dto.TopicResponseDTO;
import UMC.news.newsIntelligent.domain.topic.entity.Topic;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberTopicQueryServiceImpl implements MemberTopicQueryService {

    private final MemberTopicRepository memberTopicRepository;
    private final NewsRepository newsRepository;

    @Override
    public MemberTopicResponseDTO.MemberTopicPreviewListResDTO searchReadTopics(String keyword, Long cursor, int size, Long memberId) {
        cursor = normalizeCursor(cursor);
        size = normalizeSize(size);

        Pageable pageable = PageRequest.of(0, size);
        Slice<Topic> topicSlice = memberTopicRepository.searchReadTopicsByKeyword(memberId, keyword, cursor, pageable);

        Map<Long, TopicResponseDTO.ImageSource> srcMap = buildImageSourceMapFromSlice(topicSlice);
        return toPreviewList(topicSlice, srcMap);
    }

    @Override
    public MemberTopicResponseDTO.MemberTopicPreviewListResDTO getReadTopics(Long cursor, int size, Long memberId) {
        cursor = normalizeCursor(cursor);
        size = normalizeSize(size);

        Pageable pageable = PageRequest.of(0, size);
        Slice<Topic> topicSlice = memberTopicRepository.getReadTopicsByMemberId(memberId, cursor, pageable);

        Map<Long, TopicResponseDTO.ImageSource> srcMap = buildImageSourceMapFromSlice(topicSlice);
        return toPreviewList(topicSlice, srcMap);
    }

    @Override
    public MemberTopicResponseDTO.MemberTopicPreviewListResDTO getSubscriptionTopics(Long cursor, int size, Long memberId) {
        cursor = normalizeCursor(cursor);
        size = normalizeSize(size);

        Pageable pageable = PageRequest.of(0, size);
        Slice<Topic> topicSlice = memberTopicRepository.getSubscriptionTopicsByMemberId(memberId, cursor, pageable);

        Map<Long, TopicResponseDTO.ImageSource> srcMap = buildImageSourceMapFromSlice(topicSlice);
        return toPreviewList(topicSlice, srcMap);
    }

    // 해당 페이지 기사들 한 번에 조회 -> Map
    private Map<Long, TopicResponseDTO.ImageSource> buildImageSourceMapFromSlice(Slice<Topic> slice) {
        List<Long> topicIds = slice.getContent().stream()
                .map(Topic::getId).toList();

        if (topicIds.isEmpty()) return java.util.Collections.emptyMap();

        // OldestPerTopicProjection: (topicId, press, title) 를 가진 프로젝션
        List<OldestPerTopicProjection> oldestList = newsRepository.findOldestPerTopic(topicIds);

        return oldestList.stream().collect(
                java.util.stream.Collectors.toMap(
                        OldestPerTopicProjection::getTopicId,
                        p -> TopicResponseDTO.ImageSource.builder()
                                .press(p.getPress())
                                .title(p.getTitle())
                                .build(),
                        (a, b) -> a
                )
        );
    }

    private MemberTopicResponseDTO.MemberTopicPreviewListResDTO toPreviewList(
            Slice<Topic> slice,
            Map<Long, TopicResponseDTO.ImageSource> srcMap
    ) {
        var topics = MemberTopicConverter.toPreviewResDTOList(slice.getContent(), srcMap);

        Long nextCursor = (slice.hasNext() && !topics.isEmpty())
                ? topics.get(topics.size() - 1).id()
                : null;

        return MemberTopicResponseDTO.MemberTopicPreviewListResDTO.builder()
                .cursor(nextCursor)
                .hasNext(slice.hasNext())
                .topics(topics)
                .build();
    }

    private Long normalizeCursor(Long cursor) {
        return (cursor == null || cursor == 0) ? Long.MAX_VALUE : cursor;
    }

    // 요청 사이즈가 1보다 작으면 기본값 10, 10보다 크면 최대값 10으로 제한
    private int normalizeSize(int size) {
        return (size < 1 || size > 10) ? 10 : size;
    }

}
