package UMC.news.newsIntelligent.domain.feedback.service.command;

import UMC.news.newsIntelligent.domain.feedback.code.FeedbackErrorCode;
import UMC.news.newsIntelligent.domain.feedback.dto.FeedbackRequestDTO;
import UMC.news.newsIntelligent.domain.feedback.entity.Feedback;
import UMC.news.newsIntelligent.domain.feedback.repository.FeedbackRepository;
import UMC.news.newsIntelligent.domain.member.entity.Member;
import UMC.news.newsIntelligent.domain.member.repository.MemberRepository;
import UMC.news.newsIntelligent.global.apiPayload.code.error.GeneralErrorCode;
import UMC.news.newsIntelligent.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackCommandServiceImpl implements FeedbackCommandService {

    private final FeedbackRepository feedbackRepository;
    private final MemberRepository memberRepository;

    @Override
    public void submitFeedback(FeedbackRequestDTO.FeedbackRequest request, Long memberId) {
        // 피드백 내용이 null이거나 빈칸인 경우 customException 발생
        if (request.content() == null || request.content().isBlank()) {
            throw new CustomException(FeedbackErrorCode.EMPTY_CONTENT);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(GeneralErrorCode.NOT_FOUND_404));

        Feedback feedback = Feedback.builder()
                .member(member)
                .content(request.content())
                .build();

        feedbackRepository.save(feedback);

        /* 특정 글자 수 리미트를 정한 경우
        if (request.getContent().length() > 500) {
            throw new CustomException(FeedbackErrorCode.TOO_LONG_CONTENT);
        }
         */
    }
}
