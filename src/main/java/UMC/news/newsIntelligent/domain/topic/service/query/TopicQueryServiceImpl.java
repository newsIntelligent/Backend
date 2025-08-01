package UMC.news.newsIntelligent.domain.topic.service.query;

import UMC.news.newsIntelligent.domain.topic.entity.Topic;
import UMC.news.newsIntelligent.domain.topic.converter.TopicConverter;
import UMC.news.newsIntelligent.domain.topic.dto.TopicResponseDTO;
import UMC.news.newsIntelligent.domain.topic.repository.TopicRepository;
import UMC.news.newsIntelligent.global.apiPayload.code.error.GeneralErrorCode;
import UMC.news.newsIntelligent.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TopicQueryServiceImpl implements TopicQueryService {

    private final TopicRepository topicRepository;

    @Override
    public TopicResponseDTO.TopicPreviewListResDTO searchTopics(String keyword, Long cursor, int size) {
        cursor = normalizeCursor(cursor);
        size = normalizeSize(size);

        Pageable pageable = PageRequest.of(0, size);
        Slice<Topic> topicSlice = topicRepository.findByKeywordAndCursor(keyword, cursor, pageable);

        List<TopicResponseDTO.TopicPreviewResDTO> topicList = topicSlice.stream()
                .map(TopicConverter::toPreviewResDTO)
                .toList();

        Long nextCursor = (topicSlice.hasNext() && !topicList.isEmpty())
                ? topicList.get(topicList.size() - 1).id()
                : null;

        return TopicResponseDTO.TopicPreviewListResDTO.builder()
                .cursor(nextCursor)
                .hasNext(topicSlice.hasNext())
                .topics(topicList)
                .build();
    }

    private Long normalizeCursor(Long cursor) {
        return (cursor == null || cursor == 0) ? Long.MAX_VALUE : cursor;
    }

    // 요청 사이즈가 1보다 작으면 기본값 10, 10보다 크면 최대값 10으로 제한
    private int normalizeSize(int size) {
        return (size < 1 || size > 10) ? 10 : size;
    }


    @Override
    public TopicResponseDTO.TopicDetailsResDTO getTopicById(Long topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new CustomException(GeneralErrorCode.TOPIC_NOT_FOUND));

        return new TopicResponseDTO.TopicDetailsResDTO(
                topic.getId(),
                topic.getTopicName(),
                topic.getAiSummary(),
                topic.getImageUrl(),
                topic.getSummaryTime()
        );
    }
}
