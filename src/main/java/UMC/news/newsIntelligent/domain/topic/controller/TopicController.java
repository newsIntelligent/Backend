package UMC.news.newsIntelligent.domain.topic.controller;

import UMC.news.newsIntelligent.domain.topic.dto.TopicResponseDTO;
import UMC.news.newsIntelligent.domain.topic.entity.Topic;
import UMC.news.newsIntelligent.domain.topic.service.query.TopicQueryService;
import UMC.news.newsIntelligent.global.apiPayload.CustomResponse;
import UMC.news.newsIntelligent.global.apiPayload.code.success.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
@Validated
@Tag(name = "토픽 관련 API", description = "토픽 조회")
public class TopicController {

    private final TopicQueryService topicQueryService;

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

    @Operation(summary = "토픽 상세 페이지 조회 API by 신윤진", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토픽 상세 페이지 조회 성공")
    })
    @GetMapping("/{topicId}")
    public CustomResponse<TopicResponseDTO.TopicDetailsResDTO> getTopicDetails(@PathVariable Long topicId){
        TopicResponseDTO.TopicDetailsResDTO topicDetailsDTO = topicQueryService.getTopicById(topicId);
        return CustomResponse.onSuccess(GeneralSuccessCode.GET_TOPIC, topicDetailsDTO);
    }
}
