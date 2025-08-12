package UMC.news.newsIntelligent.domain.dailyReport.repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import UMC.news.newsIntelligent.domain.dailyReport.entity.DailyReport;

public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {
	List<DailyReport> findAllByMemberId(Long memberId);
	List<DailyReport> findByReportTime(LocalTime reportTime);
}
