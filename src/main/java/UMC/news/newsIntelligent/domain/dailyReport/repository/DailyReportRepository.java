package UMC.news.newsIntelligent.domain.dailyReport.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import UMC.news.newsIntelligent.domain.dailyReport.DailyReport;

public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {
	List<DailyReport> findAllByMemberId(Long memberId);
}
