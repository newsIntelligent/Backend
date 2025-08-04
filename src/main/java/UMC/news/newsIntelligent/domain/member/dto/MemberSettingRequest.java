package UMC.news.newsIntelligent.domain.member.dto;

import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ReportTimeRequest {

		/**
		 * "HH:mm" (00:00~23:59)
		 */
		@NotBlank
		@Pattern(
			regexp = "^([01]\\d|2[0-3]):[0-5]\\d$",
			message = "HH:mm 형식이어야 합니다"
		)
		private String time;

		public LocalTime toLocalTime() {
			return LocalTime.parse(this.time);
		}
	}
}
