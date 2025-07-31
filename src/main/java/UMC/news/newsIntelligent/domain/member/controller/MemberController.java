package UMC.news.newsIntelligent.domain.member.controller;

import UMC.news.newsIntelligent.domain.member.dto.MemberInfoDto;
import UMC.news.newsIntelligent.domain.member.service.MemberQueryService;
import UMC.news.newsIntelligent.global.apiPayload.CustomResponse;
import UMC.news.newsIntelligent.global.apiPayload.code.success.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Tag(name="회원 관련 API", description = "회원 정보 조회/수정")
public class MemberController {
    private final MemberQueryService memberQueryService;

    /* --- 회원 정보 조회 --- */
    @Operation(summary = "회원 정보 조회", description = "회원정보를 리스트로 반환하는 API입니다.")
    @GetMapping("/info/{memberId}")
    public CustomResponse<List<MemberInfoDto>> getInfo(@PathVariable Long memberId) {

        MemberInfoDto dto = memberQueryService.getInfo(memberId);

        return CustomResponse.onSuccess(
                GeneralSuccessCode.OK,
                List.of(dto)
        );
    }
}
