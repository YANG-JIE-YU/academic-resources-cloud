package cn.academic.cloud.ai.service.impl;

import cn.academic.cloud.ai.domain.entity.AiConversationLogEntity;
import cn.academic.cloud.ai.integration.MiniMaxClient;
import cn.academic.cloud.ai.mapper.AiConversationLogMapper;
import cn.academic.cloud.ai.service.AiChatService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.function.Consumer;

@Service
public class AiChatServiceImpl implements AiChatService {

    private final AiConversationLogMapper conversationLogMapper;
    private final MiniMaxClient miniMaxClient;

    public AiChatServiceImpl(AiConversationLogMapper conversationLogMapper, MiniMaxClient miniMaxClient) {
        this.conversationLogMapper = conversationLogMapper;
        this.miniMaxClient = miniMaxClient;
    }

    @Override
    public String chat(Long userId, String question) {
        return doChat(userId, question, null);
    }

    @Override
    public String chatStream(Long userId, String question, Consumer<String> onChunk) {
        return doChat(userId, question, onChunk);
    }

    private String doChat(Long userId, String question, Consumer<String> onChunk) {
        String prompt = """
                You are an academic portal assistant.
                Please answer the user's question in Chinese, keep it practical and concise.
                User question:
                %s
                """.formatted(question == null ? "" : question.trim());

        String modelName = miniMaxClient.getModelName();
        String answer = null;
        boolean streamedOutput = false;

        if (onChunk != null) {
            StringBuilder streamAnswer = new StringBuilder();
            boolean streamSuccess = miniMaxClient.streamChat(prompt, 0.3d, 1024, chunk -> {
                streamAnswer.append(chunk);
                onChunk.accept(chunk);
            });
            if (streamSuccess && StringUtils.hasText(streamAnswer.toString())) {
                answer = streamAnswer.toString();
                streamedOutput = true;
            }
        }

        if (!StringUtils.hasText(answer)) {
            answer = miniMaxClient.chat(prompt);
            if (onChunk != null && StringUtils.hasText(answer) && !streamedOutput) {
                onChunk.accept(answer);
            }
        }

        if (!StringUtils.hasText(answer)) {
            answer = "Academic assistant demo reply: keep asking about topic selection, summary polishing, keywords and retrieval.";
            modelName = "rule-based-demo";
            if (onChunk != null && !streamedOutput) {
                onChunk.accept(answer);
            }
        }

        AiConversationLogEntity log = new AiConversationLogEntity();
        log.setUserId(userId);
        log.setQuestion(question);
        log.setAnswer(answer);
        log.setModelName(modelName);
        log.setCreateTime(LocalDateTime.now());
        conversationLogMapper.insert(log);
        return answer;
    }
}
