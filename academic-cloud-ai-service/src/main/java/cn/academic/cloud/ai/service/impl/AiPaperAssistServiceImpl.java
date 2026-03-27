package cn.academic.cloud.ai.service.impl;

import cn.academic.cloud.ai.domain.entity.AiPaperTaskLogEntity;
import cn.academic.cloud.ai.integration.MiniMaxClient;
import cn.academic.cloud.ai.mapper.AiPaperTaskLogMapper;
import cn.academic.cloud.ai.service.AiPaperAssistService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class AiPaperAssistServiceImpl implements AiPaperAssistService {

    private final AiPaperTaskLogMapper paperTaskLogMapper;
    private final MiniMaxClient miniMaxClient;

    public AiPaperAssistServiceImpl(AiPaperTaskLogMapper paperTaskLogMapper, MiniMaxClient miniMaxClient) {
        this.paperTaskLogMapper = paperTaskLogMapper;
        this.miniMaxClient = miniMaxClient;
    }

    @Override
    public String summarize(Long userId, String text) {
        String cleanText = text == null ? "" : text.trim();
        String summary = null;
        if (miniMaxClient.isEnabled() && StringUtils.hasText(cleanText)) {
            String prompt = """
                    Summarize the following academic text in Chinese within 200 Chinese characters.
                    Keep the core objective, method, and conclusion.
                    Text:
                    %s
                    """.formatted(cleanText);
            summary = miniMaxClient.chat(prompt, 0.2d, 512);
        }

        if (!StringUtils.hasText(summary)) {
            summary = summarizeByRule(cleanText);
        }

        saveTaskLog(userId, "SUMMARY", cleanText, summary);
        return summary;
    }

    @Override
    public List<String> extractKeywords(Long userId, String text) {
        String cleanText = text == null ? "" : text.trim();

        List<String> result = new ArrayList<>();
        if (miniMaxClient.isEnabled() && StringUtils.hasText(cleanText)) {
            String prompt = """
                    Extract up to 10 keywords from the text.
                    Return ONLY comma-separated keywords in Chinese, no explanation.
                    Text:
                    %s
                    """.formatted(cleanText);
            String response = miniMaxClient.chat(prompt, 0.1d, 256);
            result = parseKeywords(response);
        }

        if (result.isEmpty()) {
            result = extractKeywordsByRule(cleanText);
        }

        saveTaskLog(userId, "KEYWORD", cleanText, String.join(",", result));
        return result;
    }

    private String summarizeByRule(String cleanText) {
        if (cleanText.length() <= 160) {
            return cleanText;
        }
        return cleanText.substring(0, 160) + "...";
    }

    private List<String> extractKeywordsByRule(String cleanText) {
        String[] words = cleanText.split("[\\s,，。；;、\\n\\t]+");
        Set<String> set = new LinkedHashSet<>();
        for (String word : words) {
            if (!StringUtils.hasText(word)) {
                continue;
            }
            String item = word.trim();
            if (item.length() < 2) {
                continue;
            }
            set.add(item);
            if (set.size() >= 10) {
                break;
            }
        }
        return new ArrayList<>(set);
    }

    private List<String> parseKeywords(String content) {
        if (!StringUtils.hasText(content)) {
            return new ArrayList<>();
        }
        String normalized = content
                .replace("关键词：", "")
                .replace("Keywords:", "")
                .replace("keywords:", "")
                .replace("\n", ",")
                .trim();
        String[] parts = normalized.split("[,，;；、\\s]+");
        Set<String> set = new LinkedHashSet<>();
        for (String part : parts) {
            if (!StringUtils.hasText(part)) {
                continue;
            }
            String item = part.trim();
            if (item.length() < 2) {
                continue;
            }
            set.add(item);
            if (set.size() >= 10) {
                break;
            }
        }
        return new ArrayList<>(set);
    }

    private void saveTaskLog(Long userId, String taskType, String input, String output) {
        AiPaperTaskLogEntity log = new AiPaperTaskLogEntity();
        log.setUserId(userId);
        log.setTaskType(taskType);
        log.setInputText(input);
        log.setOutputText(output);
        log.setCreateTime(LocalDateTime.now());
        paperTaskLogMapper.insert(log);
    }
}
