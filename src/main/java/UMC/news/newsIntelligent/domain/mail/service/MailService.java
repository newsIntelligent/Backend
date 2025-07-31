package UMC.news.newsIntelligent.domain.mail.service;

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

    public String generateCode() {
        return String.format("%06d", new SecureRandom().nextInt(1_000_000));
    }
    public String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");   // 32 hex
    }

    public void sendOtpMail(String to, String code, String token, OtpCode.Type type) {
        String path  = (type == OtpCode.Type.SIGNUP) ? "signup" : "login";
        String link  = "https://newsintelligent.io/%s/magic?token=%s".formatted(path, token);

        String html = """
            <div style='margin:80px;font-family:Verdana'>
              <h2>NewsIntelligent 인증 메일입니다.</h2>
              <p>아래 <b>코드</b>를 입력하거나 <b>매직링크</b>를 클릭하세요.</p>
              <h1 style='color:#3366ff'>%s</h1>
              <p><a href='%s'>➡️ 매직링크 바로가기</a> · 5분 내 1회 사용</p>
            </div>
            """.formatted(code, link);

        try {
            MimeMessage msg = mailSender.createMimeMessage();
            msg.addRecipients(Message.RecipientType.TO, to);
            msg.setSubject("[NewsIntelligent] 인증 메일");
            msg.setText(html, "utf-8", "html");
            msg.setFrom(new InternetAddress(from, "NewsIntelligent"));
            mailSender.send(msg);
        } catch (Exception e) {
            throw new IllegalStateException("메일 발송 실패", e);
        }
    }
}