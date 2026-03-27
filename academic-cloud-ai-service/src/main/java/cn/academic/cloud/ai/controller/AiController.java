package cn.academic.cloud.ai.controller;

import cn.academic.cloud.ai.domain.dto.AdminAiLogItem;
import cn.academic.cloud.ai.domain.dto.ChatRequest;
import cn.academic.cloud.ai.domain.dto.RecommendResponse;
import cn.academic.cloud.ai.domain.dto.TextProcessRequest;
import cn.academic.cloud.ai.service.AiChatService;
import cn.academic.cloud.ai.service.AiLogService;
import cn.academic.cloud.ai.service.AiPaperAssistService;
import cn.academic.cloud.ai.service.AiRecommendService;
import cn.academic.cloud.ai.support.UserHeaderContext;
import cn.academic.cloud.common.core.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiChatService aiChatService;
    private final AiPaperAssistService aiPaperAssistService;
    private final AiRecommendService aiRecommendService;
    private final AiLogService aiLogService;

    public AiController(AiChatService aiChatService,
                        AiPaperAssistService aiPaperAssistService,
                        AiRecommendService aiRecommendService,
                        AiLogService aiLogService) {
        this.aiChatService = aiChatService;
        this.aiPaperAssistService = aiPaperAssistService;
        this.aiRecommendService = aiRecommendService;
        this.aiLogService = aiLogService;
    }

    @PostMapping("/chat")
    public Result<Map<String, String>> chat(@Valid @RequestBody ChatRequest request, HttpServletRequest httpRequest) {
        Long userId = UserHeaderContext.getOptionalUserId(httpRequest);
        String answer = aiChatService.chat(userId, request.getQuestion());
        return Result.success(Map.of("answer", answer));
    }

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(@Valid @RequestBody ChatRequest request, HttpServletRequest httpRequest) {
        Long userId = UserHeaderContext.getOptionalUserId(httpRequest);
        SseEmitter emitter = new SseEmitter(0L);
        CompletableFuture.runAsync(() -> {
            try {
                aiChatService.chatStream(userId, request.getQuestion(), chunk -> sendEvent(emitter, "message", chunk));
                sendEvent(emitter, "done", "[DONE]");
                emitter.complete();
            } catch (Exception ex) {
                try {
                    sendEvent(emitter, "error", ex.getMessage() == null ? "stream failed" : ex.getMessage());
                } catch (Exception ignore) {
                    // ignore send error when client already disconnected
                }
                emitter.completeWithError(ex);
            }
        });
        return emitter;
    }

    @PostMapping("/paper/summary")
    public Result<Map<String, String>> summary(@Valid @RequestBody TextProcessRequest request,
                                               HttpServletRequest httpRequest) {
        Long userId = UserHeaderContext.getOptionalUserId(httpRequest);
        String summary = aiPaperAssistService.summarize(userId, request.getText());
        return Result.success(Map.of("summary", summary));
    }

    @PostMapping("/paper/keywords")
    public Result<List<String>> keywords(@Valid @RequestBody TextProcessRequest request,
                                         HttpServletRequest httpRequest) {
        Long userId = UserHeaderContext.getOptionalUserId(httpRequest);
        return Result.success(aiPaperAssistService.extractKeywords(userId, request.getText()));
    }

    @GetMapping("/recommend")
    public Result<RecommendResponse> recommend(@RequestParam(value = "scene", defaultValue = "HOME") String scene,
                                               @RequestParam(value = "query", required = false) String query,
                                               @RequestParam(value = "limit", defaultValue = "10") int limit,
                                               HttpServletRequest httpRequest) {
        Long userId = UserHeaderContext.getOptionalUserId(httpRequest);
        return Result.success(aiRecommendService.recommend(userId, scene, query, limit));
    }

    @GetMapping("/admin/logs")
    public Result<List<AdminAiLogItem>> logs(@RequestParam(value = "type", required = false) String type,
                                             @RequestParam(value = "limit", defaultValue = "100") int limit,
                                             HttpServletRequest httpRequest) {
        UserHeaderContext.requireAdmin(httpRequest);
        return Result.success(aiLogService.listLogs(type, limit));
    }

    private void sendEvent(SseEmitter emitter, String eventName, String data) {
        if (data == null) {
            return;
        }
        try {
            emitter.send(SseEmitter.event().name(eventName).data(data));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
