package cn.academic.cloud.ai.service;

import java.util.List;

public interface AiPaperAssistService {

    String summarize(Long userId, String text);

    List<String> extractKeywords(Long userId, String text);
}
