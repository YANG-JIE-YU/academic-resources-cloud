package cn.academic.cloud.ai.service;

import java.util.function.Consumer;

public interface AiChatService {

    String chat(Long userId, String question);

    String chatStream(Long userId, String question, Consumer<String> onChunk);
}
