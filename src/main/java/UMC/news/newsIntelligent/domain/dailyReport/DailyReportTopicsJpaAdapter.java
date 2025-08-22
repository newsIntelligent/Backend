package UMC.news.newsIntelligent.domain.dailyReport;

import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import UMC.news.newsIntelligent.domain.dailyReport.port.DailyReportTopicsPort;
import UMC.news.newsIntelligent.domain.mail.service.MailService;
import UMC.news.newsIntelligent.domain.member.entity.Member;
import UMC.news.newsIntelligent.domain.member.repository.MemberTopicRepository;
import UMC.news.newsIntelligent.domain.topic.entity.Topic;
import UMC.news.newsIntelligent.domain.topic.repository.TopicRepository;
import lombok.RequiredArgsConstructor;

@Service
@Primary
@RequiredArgsConstructor
public class DailyReportTopicsJpaAdapter implements DailyReportTopicsPort {

	private final MemberTopicRepository memberTopicRepository;
	private final TopicRepository topicRepository;
	private final MailService mailService;

	private static final int SUBSCRIBED_LIMIT = 2; // 구독 업데이트 섹션 표출 개수

	private static final DateTimeFormatter META_TIME_FMT = DateTimeFormatter.ofPattern("MM/dd HH:mm");

	@Override
	public List<DailyReportEmailTemplate.TopicCard> loadSubScribedUpdateCards(Member member) {
		Page<Topic> page = memberTopicRepository.findSubscribedTopicsOrderByUpdatedDesc(
			member.getId(),
			PageRequest.of(0, SUBSCRIBED_LIMIT)
		);
		return page.getContent().stream()
			.map(this::toCard)
			.collect(Collectors.toList());
	}

	@Override
	public List<DailyReportEmailTemplate.TopicCard> loadNewestTopicCards(Member member, int limit) {
		// 구독 섹션에 들어간 토픽 id 수집(중복 방지)
		List<DailyReportEmailTemplate.TopicCard> subscribed = loadSubScribedUpdateCards(member);
		Set<Long> excludeIds = subscribed.stream()
			.map(DailyReportEmailTemplate.TopicCard::linkUrl) // "/article?id={id}" 에서 id만 뽑아내는 대신, 안전하게 다시 조회
			.map(this::extractIdFromLinkOrReturnMinusOne)
			.filter(id -> id > 0)
			.collect(Collectors.toSet());

		Page<Topic> page;
		if (excludeIds.isEmpty()) {
			page = topicRepository.findAllOrderByUpdatedDesc(PageRequest.of(0, limit));
		} else {
			page = topicRepository.findAllExcludingIdsOrderByUpdatedDesc(excludeIds, PageRequest.of(0, limit));
		}
		return page.getContent().stream()
			.map(this::toCard)
			.collect(Collectors.toList());
	}

	/** Topic -> TopicCard 매핑 */
	private DailyReportEmailTemplate.TopicCard toCard(Topic t) {
		String base = mailService.getFrontBaseUrl();
		String baseNorm = (base != null && base.endsWith("/")) ? base.substring(0, base.length() - 1) : base;

		// 토픽 상세페이지 링크 형식: /article?id={id}
		String link = baseNorm + "/article?id=" + t.getId();

		String meta = "업데이트 " + META_TIME_FMT.format(t.getSummaryTime());
		String title = t.getTopicName();
		String summary = safeSummary(t);
		String imageUrl = t.getImageUrl();
		return new DailyReportEmailTemplate.TopicCard(title, summary, meta, link, imageUrl);
	}

	private String safeSummary(Topic t) {
		try {
			String s = t.getAiSummary();
			if (s != null && !s.isBlank()) return s;
		} catch (Exception ignore) {}
		return "";
	}

	/**
	 * id 추출 로직
	 * 링크에서 "/article?id={id}" 쿼리 파라미터인 id를 추출
	 * 추출 실패 시 -1 반환.
	 */
	private long extractIdFromLinkOrReturnMinusOne(String link) {
		if (link == null || link.isBlank()) return -1;
		try {
			URI uri = URI.create(link);
			String query = uri.getRawQuery();
			if(query == null) return -1;
			if(!query.startsWith("id=")) return -1;

			String idStr = query.substring(3);
			return Long.parseLong(idStr);
		} catch (Exception e) {
			return -1;
		}
	}
}
