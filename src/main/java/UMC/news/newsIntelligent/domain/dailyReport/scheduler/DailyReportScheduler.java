package UMC.news.newsIntelligent.domain.dailyReport.scheduler;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import UMC.news.newsIntelligent.domain.dailyReport.entity.DailyReport;
import UMC.news.newsIntelligent.domain.dailyReport.repository.DailyReportRepository;
import UMC.news.newsIntelligent.domain.dailyReport.service.DailyReportSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyReportScheduler {

	private final DailyReportRepository dailyReportRepository;
	private final DailyReportSender sender;

	@Scheduled(cron = "0 * * * * *")
	public void tick() {
		LocalTime now = LocalTime.now(ZoneId.of("Asia/Seoul"))
			.withSecond(0).withNano(0);

		List<DailyReport> targets = dailyReportRepository.findByReportTime(now);

		for (DailyReport dr : targets) {
			try {
				sender.sendFor(dr.getMember());
			} catch (Exception e) {
				log.error("DailyReport send failed. memberId={}, time={}", dr.getMember().getId(), now, e);
			}
		}

	}
}
