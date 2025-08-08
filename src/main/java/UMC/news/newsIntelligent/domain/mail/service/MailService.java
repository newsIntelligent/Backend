package UMC.news.newsIntelligent.domain.mail.service;

import UMC.news.newsIntelligent.domain.mail.EmailHtmlTemplate;
import UMC.news.newsIntelligent.domain.mail.entity.OtpCode;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}") private String from;
    @Value("${server.host.front}") private String frontUrl;

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
        String path  = (type == OtpCode.Type.SIGNUP) ? "/signup/magic" : "/login/magic";
        String link = baseUrl() + path + "?token=" + token;
        String html = EmailHtmlTemplate.renderOtpVerify(hyphenateCode(code), link);
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
}