package com.kevin.basecore.modules.ai.service.impl;

import com.kevin.basecore.modules.ai.service.AiConfigService;
import com.kevin.basecore.modules.ai.service.AiRngService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.SplittableRandom;

@Service
@RequiredArgsConstructor
public class AiRngServiceImpl implements AiRngService {

    private final AiConfigService aiConfigService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generateId() {
        int length = aiConfigService.getRngIdLength();
        String charset = aiConfigService.getRngCharset();
        StringBuilder builder = new StringBuilder();
        int bound = charset.length();
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(bound);
            builder.append(charset.charAt(index));
        }
        return builder.toString();
    }

    @Override
    public int nextInt(int bound, Long seed) {
        if (seed == null) {
            return secureRandom.nextInt(bound);
        }
        SplittableRandom random = new SplittableRandom(seed);
        return random.nextInt(bound);
    }
}
