package UMC.news.newsIntelligent.domain.mail.service;

import UMC.news.newsIntelligent.domain.mail.EmailHtmlTemplate;
import UMC.news.newsIntelligent.domain.mail.entity.OtpCode;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}") private String from;
    @Value("${server.host.front}") private String frontUrl;
    @Value("${server.host.api}") private String apiUrl;

    public String getFrontBaseUrl() { return baseUrl(); }

	public String generateCode() {
        return String.format("%06d", new SecureRandom().nextInt(1_000_000));
    }
    public String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    private String hyphenateCode(String code) {
        return (code != null && code.length() == 6)
                // 코드 표기: 하이픈 형태
                ? code.substring(0, 3) + "-" + code.substring(3)
                : code;
    }
    private String baseUrl() {
        String b = frontUrl == null ? "" : frontUrl.trim();
        return b.endsWith("/") ? b.substring(0, b.length() - 1) : b;
    }

    public void sendOtpMail(String to, String code, String token, OtpCode.Type type) {
        String path = (type == OtpCode.Type.SIGNUP)
                ? "/api/members/signup/magic"
                : "/api/members/login/magic";
        String link = baseUrl() + path + "?token=" +
                URLEncoder.encode(token, StandardCharsets.UTF_8);

        String html = EmailHtmlTemplate.renderOtp(hyphenateCode(code), link);
        sendHtml(to, "[NewsIntelligent] 이메일 주소 확인", html);
    }

    public void sendNotificationEmailChangeMail(String to, String code, String token) {
        String link  = baseUrl() + "/settings/notification-email/magic?token=" + token;
        String html = EmailHtmlTemplate.renderNotificationChange(hyphenateCode(code), link);
        sendHtml(to, "[NewsIntelligent] 알림 이메일 변경 확인", html);
    }

    // 공통 발송 로직
    private void sendHtml(String to, String title, String html) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            msg.addRecipients(Message.RecipientType.TO, to);
            msg.setSubject(title, "UTF-8");
            msg.setText(html, "UTF-8", "html");
            msg.setFrom(new InternetAddress(from, "NewsIntelligent"));
            mailSender.send(msg);
        } catch (Exception e) {
            throw new IllegalStateException("메일 발송 실패", e);
        }
    }

    /** 데일리리포트: 프로젝트 내부 로고를 인라인 첨부 **/
    public void sendDailyReport(String to, String html) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("[NewsIntelligent] Daily-Report");
            helper.setFrom(new InternetAddress(from, "NewsIntelligent"));
            helper.setText(html, true);

            // classpath: src/main/resources/mail/logo-mail.png
            ClassPathResource logo = new ClassPathResource("mail/logo-mail.png");
            helper.addInline("mailLogo", logo, "image/png");

            mailSender.send(msg);
        } catch (Exception e) {
            throw new IllegalStateException("데일리리포트 메일 발송 실패", e);
        }
    }
}