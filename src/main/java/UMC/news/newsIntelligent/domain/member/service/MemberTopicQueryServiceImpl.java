package UMC.news.newsIntelligent.domain.member.service;

import UMC.news.newsIntelligent.domain.member.converter.MemberTopicConverter;
import UMC.news.newsIntelligent.domain.member.dto.MemberTopicResponseDTO;
import UMC.news.newsIntelligent.domain.member.repository.MemberTopicRepository;
import UMC.news.newsIntelligent.domain.topic.entity.Topic;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberTopicQueryServiceImpl implements MemberTopicQueryService {

    private final MemberTopicRepository memberTopicRepository;

    @Override
    public MemberTopicResponseDTO.MemberTopicPreviewListResDTO searchReadTopics(String keyword, Long cursor, int size, Long memberId) {
        cursor = normalizeCursor(cursor);
        size = normalizeSize(size);

        Pageable pageable = PageRequest.of(0, size);
        Slice<Topic> topicSlice = memberTopicRepository.searchReadTopicsByKeyword(memberId, keyword, cursor, pageable);

        List<MemberTopicResponseDTO.MemberTopicPreviewResDTO> topicList = topicSlice.stream()
                .map(MemberTopicConverter::toPreviewResDTO)
                .toList();

        Long nextCursor = topicSlice.hasNext() ? topicList.get(topicList.size() - 1).id() : null;

        return MemberTopicResponseDTO.MemberTopicPreviewListResDTO.builder()
                .cursor(nextCursor)
                .hasNext(topicSlice.hasNext())
                .topics(topicList)
                .build();
    }

    @Override
    public MemberTopicResponseDTO.MemberTopicPreviewListResDTO getReadTopics(Long cursor, int size, Long memberId) {
        cursor = normalizeCursor(cursor);
        size = normalizeSize(size);

        Pageable pageable = PageRequest.of(0, size);
        Slice<Topic> topicSlice = memberTopicRepository.getReadTopicsByMemberId(memberId, cursor, pageable);

        List<MemberTopicResponseDTO.MemberTopicPreviewResDTO> topicList = topicSlice.stream()
                .map(MemberTopicConverter::toPreviewResDTO)
                .toList();

        Long nextCursor = topicSlice.hasNext() ? topicList.get(topicList.size() - 1).id() : null;

        return MemberTopicResponseDTO.MemberTopicPreviewListResDTO.builder()
                .cursor(nextCursor)
                .hasNext(topicSlice.hasNext())
                .topics(topicList)
                .build();
    }

    @Override
    public MemberTopicResponseDTO.MemberTopicPreviewListResDTO getSubscriptionTopics(Long cursor, int size, Long memberId) {
        cursor = normalizeCursor(cursor);
        size = normalizeSize(size);

        Pageable pageable = PageRequest.of(0, size);
        Slice<Topic> topicSlice = memberTopicRepository.getSubscriptionTopicsByMemberId(memberId, cursor, pageable);

        List<MemberTopicResponseDTO.MemberTopicPreviewResDTO> topicList = topicSlice.stream()
                .map(MemberTopicConverter::toPreviewResDTO)
                .toList();

        Long nextCursor = (topicSlice.hasNext() && !topicList.isEmpty())
                ? topicList.get(topicList.size() - 1).id()
                : null;

        return MemberTopicResponseDTO.MemberTopicPreviewListResDTO.builder()
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

}
