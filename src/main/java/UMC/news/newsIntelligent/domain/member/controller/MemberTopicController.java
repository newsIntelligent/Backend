package UMC.news.newsIntelligent.domain.member.controller;

import UMC.news.newsIntelligent.domain.member.dto.MemberTopicResponseDTO;
import UMC.news.newsIntelligent.domain.member.service.MemberTopicQueryService;
import UMC.news.newsIntelligent.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@Tag(name = "마이페이지 내에서의 토픽 목록 조회 컨트롤러", description = "마이페이지 내에서의 조회와 관련된 API들을 관리하는 컨트롤러")
public class MemberTopicController {

    private final MemberTopicQueryService memberTopicQueryService;

    @Operation(summary = "읽은 토픽 목록 내 조회 API by 서동혁", description = "읽은 토픽 목록 내 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토픽 조회 성공")
    })
    @GetMapping("/read-topics")
    public CustomResponse<MemberTopicResponseDTO.MemberTopicPreviewListResDTO> searchReadTopics(
            @RequestParam String keyword,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") @Max(10) int size
            // @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {

        // 로그인 기능 구현 이후 memberId(1) 대신 매개변수로 principalDetails
        MemberTopicResponseDTO.MemberTopicPreviewListResDTO topicResDTO =
                memberTopicQueryService.searchReadTopics(keyword, cursor, size, 1L);

        return CustomResponse.onSuccess(topicResDTO);
    }

    @Operation(summary = "읽은 토픽 목록 리스트 조회 API by 서동혁", description = "읽은 토픽 목록 리스트 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토픽 리스트 조회 성공")
    })
    @GetMapping("/read-topics")
    public CustomResponse<MemberTopicResponseDTO.MemberTopicPreviewListResDTO> getReadTopics(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") @Max(10) int size
            // @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {

        // 로그인 기능 구현 이후 memberId(1) 대신 매개변수로 principalDetails
        MemberTopicResponseDTO.MemberTopicPreviewListResDTO topicResDTO =
                memberTopicQueryService.getReadTopics(cursor, size, 1L);

        return CustomResponse.onSuccess(topicResDTO);
    }

    @Operation(summary = "구독 토픽 리스트 조회 API by 서동혁", description = "구독 토픽 리스트 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "구독 토픽 리스트 조회 성공")
    })
    @GetMapping("/subscriptions")
    public CustomResponse<MemberTopicResponseDTO.MemberTopicPreviewListResDTO> getSubscriptionTopics(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") @Max(10) int size
            // @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {

        // 로그인 기능 구현 이후 memberId(1) 대신 매개변수로 principalDetails
        MemberTopicResponseDTO.MemberTopicPreviewListResDTO topicResDTO =
                memberTopicQueryService.getSubscriptionTopics(cursor, size, 1L);

        return CustomResponse.onSuccess(topicResDTO);
    }

}
