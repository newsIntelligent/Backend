package UMC.news.newsIntelligent.domain.topic.service.query;

import UMC.news.newsIntelligent.domain.member.repository.MemberTopicRepository;
import UMC.news.newsIntelligent.domain.member.service.MemberTopicQueryService;
import UMC.news.newsIntelligent.domain.news.entity.News;
import UMC.news.newsIntelligent.domain.news.repository.NewsRepository;
import UMC.news.newsIntelligent.domain.news.repository.projection.OldestPerTopicProjection;
import UMC.news.newsIntelligent.domain.topic.converter.TopicConverter;
import UMC.news.newsIntelligent.domain.topic.dto.TopicResponseDTO;
import UMC.news.newsIntelligent.domain.topic.entity.Topic;
import UMC.news.newsIntelligent.domain.topic.repository.TopicRepository;
import UMC.news.newsIntelligent.global.apiPayload.code.error.ErrorCode;
import UMC.news.newsIntelligent.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TopicQueryServiceImpl implements TopicQueryService {

    private final TopicRepository topicRepository;
    private final NewsRepository newsRepository;
    private final MemberTopicQueryService memberTopicQueryService;
    private final MemberTopicRepository memberTopicRepository;


    @Override
    public TopicResponseDTO.TopicPreviewListResDTO searchTopics(String keyword, Long cursor, int size, Long memberId) {
        cursor = normalizeCursor(cursor);
        size = normalizeSize(size);

        Pageable pageable = PageRequest.of(0, size);
        Slice<Topic> topicSlice = topicRepository.findByKeywordAndCursor(keyword, cursor, pageable);

        Map<Long, TopicResponseDTO.ImageSource> srcMap = buildImageSourceMapFromSlice(topicSlice);
        Map<Long, Boolean> subMap = memberTopicQueryService.buildSubscribedMap(memberId, topicSlice);

        return mapSliceToPreviewListDTO(topicSlice, srcMap, subMap);
    }

    private Long normalizeCursor(Long cursor) {
        return (cursor == null || cursor == 0) ? Long.MAX_VALUE : cursor;
    }

    // 요청 사이즈가 1보다 작으면 기본값 10, 10보다 크면 최대값 10으로 제한
    private int normalizeSize(int size) {
        return (size < 1 || size > 10) ? 10 : size;
    }

    @Override
    public TopicResponseDTO.TopicPreviewListResDTO getTopicList(Long cursor, int size, Long memberId) {
        size = normalizeSize(size);

        Pageable pageable = PageRequest.of(0, size);

        LocalDateTime cursorTime = null;
        Long cursorId = null;

        // 첫 페이지: cursor == null -> 최신부터
        if (cursor != null && cursor != 0) {
            Topic last = topicRepository.findById(cursor).orElse(null);
            if (last == null) throw new CustomException(ErrorCode.CURSOR_INVALID);
            cursorTime = last.getSummaryTime();
            cursorId = last.getId();
        }

        Slice<Topic> slice = topicRepository.findByCursorOrderBySummaryTimeDesc(cursorTime, cursorId, pageable);
        Map<Long, TopicResponseDTO.ImageSource> srcMap = buildImageSourceMapFromSlice(slice);
        Map<Long, Boolean> subMap = memberTopicQueryService.buildSubscribedMap(memberId, slice);

        return mapSliceToPreviewListDTO(slice, srcMap, subMap);
    }

    @Override
    public TopicResponseDTO.TopicPreviewResDTO getTopicById(Long topicId, Long memberId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new CustomException(ErrorCode.TOPIC_NOT_FOUND));

        News source = newsRepository.findFirstByTopicIdOrderByPublishDateAscIdDesc(topicId).orElse(null);

        TopicResponseDTO.ImageSource imageSource = (source == null) ? null
                : TopicResponseDTO.ImageSource.builder()
                .press(source.getPress())
                .title(source.getTitle())
                .build();

        boolean isSub = false; // 기본 false (비로그인)
        if (memberId != null) {
            isSub = memberTopicRepository
                    .existsByMemberIdAndTopicIdAndIsSubscribeTrue(memberId, topicId); // ✅ 단건 체크
        }

        return TopicResponseDTO.TopicPreviewResDTO.builder()
                .id(topic.getId())
                .topicName(topic.getTopicName())
                .aiSummary(topic.getAiSummary())
                .summaryTime(topic.getSummaryTime())
                .imageUrl(topic.getImageUrl())
                .imageSource(imageSource)
                .isSub(isSub)
                .build();
    }

    // 해당 페이지 topicId 수집 후 각 토픽의 출처 기사 한 번에 조회 -> Map
    private Map<Long, TopicResponseDTO.ImageSource> buildImageSourceMapFromSlice(Slice<Topic> slice) {
        List<Long> topicIds = slice.getContent().stream()
                .map(Topic::getId)
                .toList();

        if (topicIds.isEmpty()) return java.util.Collections.emptyMap();

        List<OldestPerTopicProjection> sourceList = newsRepository.findOldestPerTopic(topicIds);

        return sourceList.stream().collect(
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

    private TopicResponseDTO.TopicPreviewListResDTO mapSliceToPreviewListDTO(
            Slice<Topic> slice,
            Map<Long, TopicResponseDTO.ImageSource> srcMap,
            Map<Long, Boolean> subMap
    ) {
        List<TopicResponseDTO.TopicPreviewResDTO> topics =
                TopicConverter.toPreviewResDTOList(slice.getContent(), srcMap, subMap);

        Long nextCursor = (slice.hasNext() && !topics.isEmpty())
                ? topics.get(topics.size() - 1).id()
                : null;

        return TopicResponseDTO.TopicPreviewListResDTO.builder()
                .cursor(nextCursor)
                .hasNext(slice.hasNext())
                .topics(topics)
                .build();
    }
}
