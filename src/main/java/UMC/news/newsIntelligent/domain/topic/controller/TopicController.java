package UMC.news.newsIntelligent.domain.topic.controller;

import UMC.news.newsIntelligent.domain.topic.dto.TopicResponseDTO;
import UMC.news.newsIntelligent.domain.topic.service.query.TopicQueryService;
import UMC.news.newsIntelligent.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
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

}
