package UMC.news.newsIntelligent.domain.member.service;

import UMC.news.newsIntelligent.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class NicknameService {

    private final MemberRepository memberRepository;
    private static final int MAX = 15;

    public String proposeFromEmail(String email) {
        int at = email.indexOf('@');
        String local = (at > 0) ? email.substring(0, at) : email;
        String base = local.replaceAll("[^A-Za-z0-9._-]", "");

        if (base.isBlank()) base = "user";
        if (base.length() > MAX) base = base.substring(0, MAX);

        return ensureUnique(base);
    }

    private String ensureUnique(String base) {
        String candidate = base;
        int suffix = 0;

        // 닉네임 중복 시 번호 추가
        while (memberRepository.existsByNickname(candidate)) {
            suffix++;
            String sfx = "-" + suffix;
            int cut = Math.min(base.length(), MAX - sfx.length());
            candidate = base.substring(0, cut) + sfx;

            if (suffix > 9999) { // fallback
                String rand = "-" + (10000 + new SecureRandom().nextInt(90000));
                cut = Math.min(base.length(), MAX - rand.length());
                candidate = base.substring(0, cut) + rand;
                if (!memberRepository.existsByNickname(candidate)) break;
            }
        }
        return candidate;
    }
}
