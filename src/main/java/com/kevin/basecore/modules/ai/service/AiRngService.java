package com.kevin.basecore.modules.ai.service;

public interface AiRngService {
    String generateId();
    int nextInt(int bound, Long seed);
}
