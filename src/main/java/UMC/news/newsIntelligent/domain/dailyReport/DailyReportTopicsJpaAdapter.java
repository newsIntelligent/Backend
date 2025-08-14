package UMC.news.newsIntelligent.domain.dailyReport;

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
			.map(DailyReportEmailTemplate.TopicCard::linkUrl) // "/topics/{id}" 에서 id만 뽑아내는 대신, 안전하게 다시 조회
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
		String link = base + "/topics/" + t.getId();
		String meta = "업데이트 " + META_TIME_FMT.format(t.getUpdatedAt());
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
	 * 링크에서 "/topics/{id}" 형태의 id를 추출
	 * 추출 실패 시 -1 반환.
	 */
	private long extractIdFromLinkOrReturnMinusOne(String link) {
		if (link == null) return -1;
		int idx = link.lastIndexOf("/topics/");
		if (idx < 0) return -1;
		String tail = link.substring(idx + "/topics/".length());
		int q = tail.indexOf('?');
		String idStr = (q >= 0) ? tail.substring(0, q) : tail;
		try {
			return Long.parseLong(idStr);
		} catch (NumberFormatException e) {
			return -1;
		}
	}
}
