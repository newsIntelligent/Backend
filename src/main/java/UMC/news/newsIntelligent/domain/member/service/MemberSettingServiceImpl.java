package UMC.news.newsIntelligent.domain.member.service;

import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import UMC.news.newsIntelligent.domain.dailyReport.entity.DailyReport;
import UMC.news.newsIntelligent.domain.dailyReport.repository.DailyReportRepository;
import UMC.news.newsIntelligent.domain.member.dto.MemberSettingResponse;
import UMC.news.newsIntelligent.domain.member.entity.Member;
import UMC.news.newsIntelligent.domain.member.repository.MemberRepository;
import UMC.news.newsIntelligent.global.apiPayload.code.error.ErrorCode;
import UMC.news.newsIntelligent.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberSettingServiceImpl implements MemberSettingService {

	private final MemberRepository memberRepository;
	private final DailyReportRepository dailyReportRepository;

	private Member findMember(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
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

		if (enabled && m.getDailyReports().isEmpty()) {
			addReportTime(memberId, LocalTime.of(11, 0));
		}

		m.setDailyReportAlert(enabled);
		memberRepository.save(m);
	}

	@Override
	@Transactional(readOnly = true)
	public MemberSettingResponse getAllSettings(Long memberId) {
		Member m = findMember(memberId);

		List<LocalTime> reportTimes = dailyReportRepository.findAllByMemberId(memberId).stream()
			.map(DailyReport::getReportTime)
			.toList();

		return new MemberSettingResponse(
			m.getSubscribeTopicAlert(),
			m.getReadTopicAlert(),
			m.getDailyReportAlert(),
			reportTimes
		);
	}

	@Override
	public void addReportTime(Long memberId, LocalTime reportTime) {
		Member m = findMember(memberId);

		List<DailyReport> existing = dailyReportRepository.findAllByMemberId(memberId);
		// 시간 추가 최대 3개
		if(existing.size() >= 3) {
			throw new CustomException(ErrorCode.ADD_TIME_EXCEED_MAXIMUM);
		}
		// 시간 중복 추가 방지 검증
		boolean dup = existing.stream()
			.anyMatch(dr -> dr.getReportTime().equals(reportTime));
		if (dup) {
			throw new CustomException(ErrorCode.ADD_TIME_OVERLAP);
		}
		DailyReport report = DailyReport.of(m, reportTime);
		dailyReportRepository.save(report);
	}

	@Override
	public void removeReportTime(Long memberId, Long timeId) {
		DailyReport report = dailyReportRepository.findById(timeId)
			.orElseThrow(() -> new CustomException(ErrorCode.DAILY_REPORT_NOT_FOUND));
		if (!report.getMember().getId().equals(memberId)) {
			throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
		}
		dailyReportRepository.delete(report);
	}
}
