package UMC.news.newsIntelligent.domain.mail;

public final class EmailHtmlTemplate {

    private EmailHtmlTemplate() {}

    // 공통 이메일 html 템플릿
    private static final String BASE = """
        <!DOCTYPE html>
        <html lang="ko">
        <head>
          <meta charset="UTF-8" />
          <meta name="viewport" content="width=device-width, initial-scale=1.0" />
          <title>%s</title>
          <style>
            body {
              font-family: 'Pretendard', sans-serif;
              background-color: #f5f5f5;
              margin: 0;
              padding: 0;
              display: flex;
              justify-content: center;
              align-items: center;
              height: 100vh;
            }
            .container {
              background: #fff;
              padding: 32px;
              width: 400px;
              border-radius: 8px;
              box-shadow: 0 4px 20px rgba(0,0,0,0.1);
            }
            .logo {
              color: #174347;
              font-size: 24px;
              font-weight: bold;
              margin-bottom: 24px;
            }
            .title {
              font-size: 20px;
              font-weight: 700;
              margin-bottom: 8px;
            }
            .desc {
              font-size: 14px;
              color: #333;
              line-height: 1.5;
              margin-bottom: 24px;
            }
            .code-box {
              background: #f4f4f4;
              padding: 24px;
              text-align: center;
              font-size: 32px;
              font-weight: 600;
              color: #555;
              letter-spacing: 4px;
              border-radius: 8px;
              margin-bottom: 24px;
            }
            .login-btn {
              display: block;
              text-align: center;
              background-color: #4da3b6;
              color: white;
              padding: 12px;
              text-decoration: none;
              border-radius: 6px;
              font-size: 14px;
              font-weight: 600;
              margin-bottom: 16px;
            }
            .login-btn:hover {
              background-color: #3e92a1;
            }
            .footer {
              font-size: 12px;
              color: #666;
              line-height: 1.4;
            }
          </style>
        </head>
        <body>
          <div class="container">
            <div class="logo">News Intelligent</div>
            <div class="title">%s</div>
            <div class="desc">%s</div>
            <div class="code-box">%s</div>
            <a class="login-btn" href="%s">%s</a>
            <div class="footer">
              이메일을 요청하지 않았다더라도 걱정하지 마세요.<br />
              이메일을 무시하면 됩니다.
            </div>
          </div>
        </body>
        </html>
        """;

    private static String render(String pageTitle, String title, String desc, String codeText, String linkUrl, String buttonText) {
        return BASE.formatted(pageTitle, title, desc, codeText, linkUrl, buttonText);
    }

    // 회원가입 & 로그인
    public static String renderOtpVerify(String codeText, String linkUrl) {
        return render(
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
        return render(
                "알림 이메일 변경 확인",
                "알림 이메일 변경 확인",
                "아래 확인 코드를 입력하시거나 버튼을 클릭해 알림 이메일 변경을 완료해 주세요.",
                codeText,
                linkUrl,
                "이메일 변경 확인"
        );
    }
}
