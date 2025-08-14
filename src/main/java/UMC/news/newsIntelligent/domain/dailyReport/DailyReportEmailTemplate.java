package UMC.news.newsIntelligent.domain.dailyReport;

import java.util.ArrayList;
import java.util.List;

public class DailyReportEmailTemplate {

	private DailyReportEmailTemplate() {
	}

	public record TopicCard(
		String title,
		String summary,
		String meta,    // 예: 업데이트 05/03 23:50 · 이미지 조선일보 "고양이가 크게 우는..."
		String linkUrl,    // 토픽 상세 페이지로 가는 절대 URL
		String imageUrl        // 토픽 썸네일 사진
	) {
	}

	/**
	 * 이메일 HTML 생성
	 *
	 * @param pageTitle       HTML <title>pageTitle</title>
	 * @param mainUrl         로고/버튼 클릭 시 이동할 메인 페이지 링크
	 * @param logoUrl         헤더 로고 이미지 링크
	 * @param subscribedCards "구독한 토픽 업데이트"용 카드 목록 (없으면 섹션을 출력하지 않음)
	 * @param newCards        "새로운 토픽"용 카드 목록 (최대 2개만 노출)
	 */
	public static String render(
		String pageTitle,
		String mainUrl,
		String logoUrl,
		List<TopicCard> subscribedCards,
		List<TopicCard> newCards
	) {

		String subscribedSection = buildSection(
			"구독한 토픽 업데이트 >",
			subscribedCards,
			false    // 비어있으면 헤더 미출력
		);
		// 새로운 토픽 최대 2개 제한
		List<TopicCard> topMax2 = (newCards == null) ? List.of()
			: newCards.subList(0, Math.min(2, newCards.size()));
		String newSection = buildSection(
			"새로운 토픽 >",
			topMax2,
			false    // 비어있으면 헤더 미출력
		);

		return """
			<!DOCTYPE html>
			<html lang="ko">
			<head>
			  <meta charset="UTF-8">
			  <title>%s</title>
			  <meta name="viewport" content="width=device-width, initial-scale=1">
			</head>
			<body style="margin:0; padding:0; background-color:#f5f5f5;">
			  <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background-color:#f5f5f5;">
			    <tr>
			      <td align="center" style="padding:24px;">
			        <!-- 고정폭 400px 컨테이너 -->
			        <table role="presentation" width="400" cellspacing="0" cellpadding="0"
			               style="background:#ffffff; border-radius:8px; font-family: Arial, Helvetica, sans-serif;">
			          <!-- 헤더(로고 + 사이트명, 메인으로 이동) -->
			          <tr>
			            <td align="left" style="padding:18px 20px;">
			              <a href="%s" style="text-decoration:none; display:inline-block;">
			                <table role="presentation" cellspacing="0" cellpadding="0">
			                  <tr>
			                    <td style="vertical-align:middle; padding-right:10px;">
			                      <img src="%s" width="28" height="28" alt="News Intelligent"
			                           style="display:block;">
			                    </td>
			                    <td style="vertical-align:middle;">
			                      <div style="font-size:28px; font-weight:700; color:#07525E; line-height:1;">
			                        News Intelligent
			                      </div>
			                    </td>
			                  </tr>
			                </table>
			              </a>
			            </td>
			          </tr>
			
			          %s  <!-- 구독한 토픽 업데이트 섹션 (없으면 비움) -->
			
			          %s  <!-- 새로운 토픽 섹션 (최대 2개, 없으면 비움) -->
			
			          <!-- CTA -->
			          <tr>
			            <td style="padding:10px 20px 22px 20px;">
			              <a href="%s"
			                 style="display:block; text-align:center; background-color:#0EA5BF; color:#ffffff; padding:12px 16px; text-decoration:none; border-radius:16px; font-size:14px; font-weight:600;">
			                뉴스 인텔리전트에서 더 많은 토픽 보기 →
			              </a>
			            </td>
			          </tr>
			
			          <!-- footer -->
			          <tr>
			            <td style="padding:14px 20px 20px 20px; font-size:12px; color:#666; line-height:1.6;">
			              메일 수신을 원치 않으시면
			              <a href="null" style="color:#0EA5BF; text-decoration:underline;">수신거부</a>
			              를 클릭하세요.
			            </td>
			          </tr>
			        </table>
			      </td>
			    </tr>
			  </table>
			</body>
			</html>
			""".formatted(
			escapeHtml(pageTitle),
			mainUrl,
			logoUrl,
			subscribedSection,
			newSection,
			mainUrl
		);
	}

	/** 섹션(헤더 + 카드목록) 생성 */
	private static String buildSection(String headerText, List<TopicCard> cards, boolean showWhenEmpty) {
		List<TopicCard> safe = (cards == null) ? List.of() : new ArrayList<>(cards);
		if (safe.isEmpty() && !showWhenEmpty)
			return "";

		StringBuilder cardsHtml = new StringBuilder();
		for (TopicCard c : safe) {
			String imageCell = (c.imageUrl() == null || c.imageUrl().isBlank())
				? ""
				: """
				<td width="72" valign="top" style="padding-right:12px;">
				  <a href="%s" style="text-decoration:none;">
				    <img src="%s" width="72" height="72" alt="토픽 이미지"
				         style="display:block; border-radius:12px;">
				  </a>
				</td>
				""".formatted(c.linkUrl(), c.imageUrl());

			cardsHtml.append("""
				    <tr>
				      <td style="padding:14px 18px;">
				        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0"
				               style="border:1px solid #909090;border-radius:12px;">
				          <tr>
				            <td style="padding:16px 16px 8px 16px; font-size:12px; color:#909090;">%s</td>
				          </tr>
				          <tr>
				            <td style="padding:0 16px 16px 16px;">
				              <table role="presentation" width="100%%" cellspacing="0" cellpadding="0">
				                <tr>
				                  %s
				                  <td valign="top">
				                    <div style="font-size:20px; font-weight:700; color:#1d1d1d; line-height:1.3;">
				                      <a href="%s" style="color:#1d1d1d; text-decoration:none;">%s</a>
				                    </div>
				                    <!-- 얇은 회색 선 -->
				                    <div style="height:1px; background:#E6E6E6; margin:8px 0;"></div>
				                    <div style="font-size:14px; color:#1d1d1d; line-height:1.5;">
				                      %s
				                    </div>
				                  </td>
				                </tr>
				              </table>
				            </td>
				          </tr>
				        </table>
				      </td>
				    </tr>
				""".formatted(
				escapeHtml(c.meta()),
				imageCell,
				c.linkUrl(),
				escapeHtml(c.title()),
				escapeHtml(c.summary())
			));
		}

		String headerRow = """
			    <tr>
			      <td style="padding:0 20px 6px 20px; font-size:20px; color:#1d1d1d; font-weight:600;">
			        %s
			      </td>
			    </tr>
			""".formatted(escapeHtml(headerText));

		return headerRow + cardsHtml;
	}

	private static String escapeHtml(String s) {
		if (s == null)
			return "";
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}
}
