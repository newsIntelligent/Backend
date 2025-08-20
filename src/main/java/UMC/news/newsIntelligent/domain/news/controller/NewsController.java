package UMC.news.newsIntelligent.domain.news.controller;

import UMC.news.newsIntelligent.domain.news.dto.NewsResponseDTO;
import UMC.news.newsIntelligent.domain.news.service.NewsQueryServiceImpl;
import UMC.news.newsIntelligent.domain.topic.dto.TopicResponseDTO;
import UMC.news.newsIntelligent.domain.topic.service.query.TopicQueryService;
import UMC.news.newsIntelligent.global.apiPayload.CustomResponse;
import UMC.news.newsIntelligent.global.apiPayload.code.success.SuccessCode;
import UMC.news.newsIntelligent.global.config.security.PrincipalUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/topic")
@Tag(name = "토픽 상세 페이지 조회 API", description = "출처 기사 조회")
public class NewsController {

    private final NewsQueryServiceImpl newsQueryServiceImpl;
    private final TopicQueryService topicQueryService;

    @Operation(summary = "토픽 상세 페이지 조회 API", description = "토픽 상세 페이지를 조회하는 API입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토픽 상세 페이지 조회 성공")
    })
    @GetMapping("/{topicId}")
    public CustomResponse<TopicResponseDTO.TopicPreviewResDTO> getTopicDetails(
            @PathVariable Long topicId,
            @AuthenticationPrincipal PrincipalUserDetails principal
            ){
        Long memberId = (principal == null) ? null : principal.getMemberId();
        TopicResponseDTO.TopicPreviewResDTO topicDetailsDTO = topicQueryService.getTopicById(topicId, memberId);
        return CustomResponse.onSuccess(SuccessCode.GET_TOPIC, topicDetailsDTO);
    }

    @Operation(summary = "토픽 상세 페이지 - 출처 기사 목록 조회 API", description = "주제 생성에 사용된 출처 기사 목록을 반환하는 API입니다")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "토픽 출처 기사 목록 조회 성공")
    })
    @GetMapping("/{topicId}/related")
    public CustomResponse<NewsResponseDTO.NewsResDTO> getRelatedNews(
            @PathVariable Long topicId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "3") int size
    ) {
        NewsResponseDTO.NewsResDTO response = newsQueryServiceImpl.getRelatedNews(topicId, lastId, size);
        return CustomResponse.onSuccess(SuccessCode.GET_NEWS, response);
    }

    @Operation(summary = "최신 수정 보도 조회 API", description = "최신 수정된 기사 수가 3개 이상인 최신 토픽 하나를 반환합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "토픽 출처 기사 목록 조회 성공")
    })
    @GetMapping("/latest")
    public CustomResponse<NewsResponseDTO.TopicQualifiedListResDTO> getLatestTopic(
            @AuthenticationPrincipal PrincipalUserDetails principal
    ) {
        Long memberId = (principal == null) ? null : principal.getMemberId();
        NewsResponseDTO.TopicQualifiedListResDTO latestTopicNews = newsQueryServiceImpl.getLatestTopicNews(memberId);
        return CustomResponse.onSuccess(SuccessCode.GET_LATEST_TOPIC, latestTopicNews);
    }

}