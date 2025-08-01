package UMC.news.newsIntelligent.domain.member.controller;

import UMC.news.newsIntelligent.domain.member.dto.MemberTopicResponseDTO;
import UMC.news.newsIntelligent.domain.member.entity.Member;
import UMC.news.newsIntelligent.domain.member.service.MemberTopicQueryService;
import UMC.news.newsIntelligent.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@Tag(name = "마이페이지 내 조회 관련 API", description = "마이페이지 내에서 읽은 토픽 및 구독 토픽 조회")
public class MemberTopicController {

    private final MemberTopicQueryService memberTopicQueryService;

    /* --- 읽은 토픽 조회 --- */
    @Operation(summary = "읽은 토픽 목록 내 조회", description = "읽은 토픽 목록을 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토픽 조회 성공")
    })
    @GetMapping("/read-topic")
    public CustomResponse<MemberTopicResponseDTO.MemberTopicPreviewListResDTO> searchReadTopics(
            @RequestParam String keyword,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") @Max(10) int size,
            @AuthenticationPrincipal Member member
    ) {

        MemberTopicResponseDTO.MemberTopicPreviewListResDTO topicResDTO =
                memberTopicQueryService.searchReadTopics(keyword, cursor, size, member.getId());

        return CustomResponse.onSuccess(topicResDTO);
    }

    /* --- 읽은 토픽 리스트 조회 --- */
    @Operation(summary = "읽은 토픽 목록 리스트 조회", description = "읽은 토픽 목록 리스트를 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토픽 리스트 조회 성공")
    })
    @GetMapping("/read-topics")
    public CustomResponse<MemberTopicResponseDTO.MemberTopicPreviewListResDTO> getReadTopics(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") @Max(10) int size,
            @AuthenticationPrincipal Member member
    ) {

        MemberTopicResponseDTO.MemberTopicPreviewListResDTO topicResDTO =
                memberTopicQueryService.getReadTopics(cursor, size, member.getId());

        return CustomResponse.onSuccess(topicResDTO);
    }

    /* --- 구독 토픽 리스트 조회 --- */
    @Operation(summary = "구독 토픽 리스트 조회", description = "구독 토픽 리스트를 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "구독 토픽 리스트 조회 성공")
    })
    @GetMapping("/subscriptions")
    public CustomResponse<MemberTopicResponseDTO.MemberTopicPreviewListResDTO> getSubscriptionTopics(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") @Max(10) int size,
            @AuthenticationPrincipal Member member
    ) {

        MemberTopicResponseDTO.MemberTopicPreviewListResDTO topicResDTO =
                memberTopicQueryService.getSubscriptionTopics(cursor, size, member.getId());

        return CustomResponse.onSuccess(topicResDTO);
    }

}
