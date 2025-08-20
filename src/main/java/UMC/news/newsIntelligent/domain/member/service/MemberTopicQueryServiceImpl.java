package UMC.news.newsIntelligent.domain.member.service;

import UMC.news.newsIntelligent.domain.member.dto.MemberTopicResponseDTO;
import UMC.news.newsIntelligent.domain.member.repository.MemberTopicRepository;
import UMC.news.newsIntelligent.domain.member.support.TopicQueryUtils;
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

import static UMC.news.newsIntelligent.domain.member.converter.MemberTopicConverter.toPreviewList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberTopicQueryServiceImpl implements MemberTopicQueryService {

    private final MemberTopicRepository memberTopicRepository;
    private final NewsRepository newsRepository;

    @Override
    public MemberTopicResponseDTO.MemberTopicPreviewListResDTO searchReadTopics(
            String keyword, Long cursor, int size, Long memberId
    ) {
        cursor = normalizeCursor(cursor);
        size = normalizeSize(size);

        Pageable pageable = PageRequest.of(0, size);
        Slice<Topic> topicSlice =
                memberTopicRepository.searchReadTopicsByKeyword(memberId, keyword, cursor, pageable);

        Map<Long, TopicResponseDTO.ImageSource> srcMap = buildImageSourceMapFromSlice(topicSlice);
        Map<Long, Boolean> subMap =
                TopicQueryUtils.buildSubscribedMap(memberId, topicSlice, memberTopicRepository);

        return toPreviewList(topicSlice, srcMap, subMap);
    }

    @Override
    public MemberTopicResponseDTO.MemberTopicPreviewListResDTO getReadTopics(
            Long cursor, int size, Long memberId
    ) {
        cursor = normalizeCursor(cursor);
        size = normalizeSize(size);

        Pageable pageable = PageRequest.of(0, size);
        Slice<Topic> topicSlice =
                memberTopicRepository.getReadTopicsByMemberId(memberId, cursor, pageable);

        Map<Long, TopicResponseDTO.ImageSource> srcMap = buildImageSourceMapFromSlice(topicSlice);
        Map<Long, Boolean> subMap =
                TopicQueryUtils.buildSubscribedMap(memberId, topicSlice, memberTopicRepository);

        return toPreviewList(topicSlice, srcMap, subMap);
    }

    @Override
    public MemberTopicResponseDTO.MemberTopicPreviewListResDTO getSubscriptionTopics(
            Long cursor, int size, Long memberId
    ) {
        cursor = normalizeCursor(cursor);
        size = normalizeSize(size);

        Pageable pageable = PageRequest.of(0, size);
        Slice<Topic> topicSlice =
                memberTopicRepository.getSubscriptionTopicsByMemberId(memberId, cursor, pageable);

        Map<Long, TopicResponseDTO.ImageSource> srcMap = buildImageSourceMapFromSlice(topicSlice);
        Map<Long, Boolean> subMap =
                TopicQueryUtils.buildSubscribedMap(memberId, topicSlice, memberTopicRepository);

        return toPreviewList(topicSlice, srcMap, subMap);
    }


    private Map<Long, TopicResponseDTO.ImageSource> buildImageSourceMapFromSlice(Slice<Topic> slice) {
        List<Long> topicIds = slice.getContent().stream()
                .map(Topic::getId)
                .toList();

        if (topicIds.isEmpty()) return java.util.Collections.emptyMap();

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

    private Long normalizeCursor(Long cursor) {
        return (cursor == null || cursor == 0) ? Long.MAX_VALUE : cursor;
    }

    private int normalizeSize(int size) {
        return (size < 1 || size > 10) ? 10 : size;
    }
}