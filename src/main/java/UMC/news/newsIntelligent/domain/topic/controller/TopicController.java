package UMC.news.newsIntelligent.domain.topic.controller;

import UMC.news.newsIntelligent.domain.member.service.MemberTopicCommandService;
import UMC.news.newsIntelligent.domain.topic.dto.TopicResponseDTO;
import UMC.news.newsIntelligent.domain.topic.service.query.TopicQueryService;
import UMC.news.newsIntelligent.global.apiPayload.CustomResponse;
import UMC.news.newsIntelligent.global.apiPayload.code.success.GeneralSuccessCode;
import UMC.news.newsIntelligent.global.config.security.PrincipalUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
@Validated
@Tag(name = "토픽 관련 API", description = "토픽 조회 및 구독/읽음 처리를 관리")
public class TopicController {

    private final TopicQueryService topicQueryService;
    private final MemberTopicCommandService memberTopicCommandService;

    /* --- 토픽 조회 --- */
    @Operation(summary = "토픽 조회", description = "토픽을 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토픽 조회 성공")
    })
    @GetMapping("/search")
    public CustomResponse<TopicResponseDTO.TopicPreviewListResDTO> searchTopics(
            @RequestParam String keyword,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") @Max(10) int size
    ) {
        TopicResponseDTO.TopicPreviewListResDTO topicResDTO = topicQueryService.searchTopics(keyword, cursor, size);

        return CustomResponse.onSuccess(topicResDTO);
    }

    @Operation(summary = "토픽 읽음 처리 API", description = "<p>사용자가 읽은 토픽에 대해서 읽음 처리를 합니다."
        + "<p>토픽 조회 후 읽지 않은 토픽(isRead=false)일 경우만 호출합니다.")
    @PatchMapping("/{topicId}/read")
    public CustomResponse<Void> markTopicRead(
        @AuthenticationPrincipal PrincipalUserDetails principal,
        @PathVariable Long topicId
    ) {
        memberTopicCommandService.markRead(principal.getMemberId(), topicId);
        return CustomResponse.onSuccess(GeneralSuccessCode.OK, null);
    }

    @Operation(summary = "토픽 구독 요청 API", description = "<p>회원이 특정 토픽을 구독합니다.")
    @PostMapping("/{topicId}/subscribe")
    public CustomResponse<Void> subscribeTopic(
        @AuthenticationPrincipal PrincipalUserDetails principal,
        @PathVariable Long topicId
    ) {
        memberTopicCommandService.subscribe(principal.getMemberId(), topicId);
        return CustomResponse.onSuccess(GeneralSuccessCode.OK, null);
    }

    @Operation(summary = "토픽 구독 취소 API", description = "<p>회원이 특정 토픽 구독을 취소합니다.")
    @DeleteMapping("/{topicId}/unsubscribe")
    public CustomResponse<Void> unsubscribeTopic(
        @AuthenticationPrincipal PrincipalUserDetails principal,
        @PathVariable Long topicId
    ) {
        memberTopicCommandService.unsubscribe(principal.getMemberId(), topicId);
        return CustomResponse.onSuccess(GeneralSuccessCode.OK, null);
    }

}
