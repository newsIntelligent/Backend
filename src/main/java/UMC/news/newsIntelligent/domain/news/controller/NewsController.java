package UMC.news.newsIntelligent.domain.news.controller;

import UMC.news.newsIntelligent.domain.news.dto.NewsResponseDTO;
import UMC.news.newsIntelligent.domain.news.service.NewsService;
import UMC.news.newsIntelligent.global.apiPayload.CustomResponse;
import UMC.news.newsIntelligent.global.apiPayload.code.success.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/topic")
@Tag(name = "토픽 상세 페이지 - 기사 출처 관련 API", description = "출처 기사 조회")
public class NewsController {

    private final NewsService newsService;

    @Operation(summary = "토픽 상세 페이지 - 출처 기사 목록 조회", description = "주제 생성에 사용된 출처 기사 목록을 반환하는 API입니다..")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "토픽 출처 기사 목록 조회 성공")
    })
    @GetMapping("/{topicId}/related")
    public CustomResponse<NewsResponseDTO.NewsResDTO> getRelatedNews(
            @PathVariable Long topicId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "3") int size
    ) {
        NewsResponseDTO.NewsResDTO response = newsService.getRelatedNews(topicId, lastId, size);
        return CustomResponse.onSuccess(GeneralSuccessCode.GET_TOPIC, response);
    }
}
