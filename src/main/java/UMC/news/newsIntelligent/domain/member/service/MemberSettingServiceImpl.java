package UMC.news.newsIntelligent.domain.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import UMC.news.newsIntelligent.domain.member.dto.MemberSettingResponse;
import UMC.news.newsIntelligent.domain.member.entity.Member;
import UMC.news.newsIntelligent.domain.member.repository.MemberRepository;
import UMC.news.newsIntelligent.global.apiPayload.code.error.GeneralErrorCode;
import UMC.news.newsIntelligent.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberSettingServiceImpl implements MemberSettingService {

	private final MemberRepository memberRepository;

	private Member findMember(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(GeneralErrorCode.NOT_FOUND_404));
	}

	@Override
	public void setSubscribeNotification(Long memberId, Boolean enabled) {
		Member m = findMember(memberId);
		m.setSubscribeTopicAlert(enabled);
	}

	@Override
	public void setReadTopicNotification(Long memberId, Boolean enabled) {
		Member m = findMember(memberId);
		m.setReadTopicAlert(enabled);
	}

	@Override
	public void setDailyReportSend(Long memberId, Boolean enabled) {
		Member m = findMember(memberId);
		m.setDailyReportAlert(enabled);
	}
}
