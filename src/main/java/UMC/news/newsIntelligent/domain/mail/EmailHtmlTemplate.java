package UMC.news.newsIntelligent.domain.mail;

public final class EmailHtmlTemplate {

    private EmailHtmlTemplate() {}

    private static String base(String pageTitle,
                               String title,
                               String desc,
                               String codeText,
                               String linkUrl,
                               String buttonText) {
        return """
        <!DOCTYPE html>
        <html lang="ko">
        <head>
          <meta charset="UTF-8">
          <title>%s</title>
        </head>
        <body style="margin:0; padding:0; background-color:#f5f5f5;">
          <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background-color:#f5f5f5;">
            <tr>
              <td align="center" style="padding:24px;">
                <table role="presentation" width="400" cellspacing="0" cellpadding="0"
                       style="background:#ffffff; border-radius:8px; font-family: Arial, Helvetica, sans-serif;">
                  <tr>
                    <td style="padding:24px 24px 8px 24px; color:#174347; font-size:24px; font-weight:bold;">
                      News Intelligent
                    </td>
                  </tr>
                  <tr>
                    <td style="padding:0 24px 8px 24px; font-size:20px; font-weight:700; color:#000;">
                      %s
                    </td>
                  </tr>
                  <tr>
                    <td style="padding:0 24px 24px 24px; font-size:14px; color:#333; line-height:1.5;">
                      %s
                    </td>
                  </tr>
                  <tr>
                    <td style="padding:0 24px 24px 24px;">
                      <div style="background:#f4f4f4; padding:24px; text-align:center; font-size:32px; font-weight:600; color:#555; letter-spacing:4px; border-radius:8px;">
                        %s
                      </div>
                    </td>
                  </tr>
                  <tr>
                    <td style="padding:0 24px 16px 24px;">
                      <a href="%s"
                         style="display:block; text-align:center; background-color:#4da3b6; color:#ffffff; padding:12px; text-decoration:none; border-radius:6px; font-size:14px; font-weight:600;">
                        %s
                      </a>
                    </td>
                  </tr>
                  <tr>
                    <td style="padding:0 24px 24px 24px; font-size:12px; color:#666; line-height:1.4;">
                      이메일을 요청하지 않았다더라도 걱정하지 마세요.<br>
                      이메일을 무시하면 됩니다.
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
          </table>
        </body>
        </html>
        """.formatted(pageTitle, title, desc, codeText, linkUrl, buttonText);
    }

    // 회원가입 & 로그인
    public static String renderOtp(String codeText, String linkUrl) {
        return base(
                "이메일 주소 확인",
                "이메일 주소 확인",
                "여기에 확인 코드가 있습니다. 열린 브라우저 창에 복사하거나 아래 링크를 클릭하여 이 이메일 주소를 확인할 수 있습니다.",
                codeText,
                linkUrl,
                "확인 및 로그인"
        );
    }

    // 알림 이메일 변경
    public static String renderNotificationChange(String codeText, String linkUrl) {
        return base(
                "알림 이메일 변경 확인",
                "알림 이메일 변경 확인",
                "아래 확인 코드를 입력하시거나 버튼을 클릭해 알림 이메일 변경을 완료해 주세요.",
                codeText,
                linkUrl,
                "이메일 변경 확인"
        );
    }
}
