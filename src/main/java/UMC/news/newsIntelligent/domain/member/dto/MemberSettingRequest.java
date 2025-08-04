package UMC.news.newsIntelligent.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberSettingRequest {

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ToggleRequest {
		private Boolean enabled;
	}
}
