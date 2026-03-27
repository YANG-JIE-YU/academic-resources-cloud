package cn.academic.cloud.ai.service.impl;

import cn.academic.cloud.ai.client.PaperClient;
import cn.academic.cloud.ai.client.UserClient;
import cn.academic.cloud.ai.domain.dto.RecommendResponse;
import cn.academic.cloud.ai.domain.entity.AiRecommendLogEntity;
import cn.academic.cloud.ai.integration.MiniMaxClient;
import cn.academic.cloud.ai.mapper.AiRecommendLogMapper;
import cn.academic.cloud.ai.service.AiRecommendService;
import cn.academic.cloud.common.core.Result;
import cn.academic.cloud.common.dto.FavoriteDTO;
import cn.academic.cloud.common.dto.PaperSimpleDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AiRecommendServiceImpl implements AiRecommendService {

    private final UserClient userClient;
    private final PaperClient paperClient;
    private final AiRecommendLogMapper recommendLogMapper;
    private final ObjectMapper objectMapper;
    private final MiniMaxClient miniMaxClient;

    public AiRecommendServiceImpl(UserClient userClient,
                                  PaperClient paperClient,
                                  AiRecommendLogMapper recommendLogMapper,
                                  ObjectMapper objectMapper,
                                  MiniMaxClient miniMaxClient) {
        this.userClient = userClient;
        this.paperClient = paperClient;
        this.recommendLogMapper = recommendLogMapper;
        this.objectMapper = objectMapper;
        this.miniMaxClient = miniMaxClient;
    }

    @Override
    public RecommendResponse recommend(Long userId, String scene, String query, int limit) {
        int validLimit = Math.max(limit, 1);
        String trigger = StringUtils.hasText(query) ? query.trim() : "latest";

        List<PaperSimpleDTO> papers = Collections.emptyList();
        if (StringUtils.hasText(query)) {
            trigger = optimizeQuery(scene, query.trim());
            papers = dataOrEmpty(paperClient.search(trigger, validLimit));
        }

        if (CollectionUtils.isEmpty(papers) && userId != null) {
            Result<List<FavoriteDTO>> favoriteResult = userClient.favorites(userId);
            List<FavoriteDTO> favorites = favoriteResult == null ? Collections.emptyList() : favoriteResult.getData();
            if (!CollectionUtils.isEmpty(favorites)) {
                FavoriteDTO first = favorites.get(0);
                String keyword = StringUtils.hasText(first.getTargetTitle()) ? first.getTargetTitle() : first.getTargetType();
                trigger = optimizeQuery(scene, keyword);
                papers = dataOrEmpty(paperClient.search(trigger, validLimit));
            }
        }

        if (CollectionUtils.isEmpty(papers)) {
            papers = dataOrEmpty(paperClient.latest(validLimit));
        }
        papers = rerankByModel(scene, trigger, papers);
        if (papers.size() > validLimit) {
            papers = new ArrayList<>(papers.subList(0, validLimit));
        }

        saveLog(userId, scene, trigger, papers);

        RecommendResponse response = new RecommendResponse();
        response.setScene(scene);
        response.setTrigger(trigger);
        response.setPapers(papers);
        return response;
    }

    private String optimizeQuery(String scene, String rawQuery) {
        if (!miniMaxClient.isEnabled() || !StringUtils.hasText(rawQuery)) {
            return rawQuery;
        }
        String prompt = """
                You are helping an academic portal build retrieval keywords.
                Scene: %s
                User input: %s
                Rewrite the user input as one concise Chinese search query.
                Return only the rewritten query.
                """.formatted(scene, rawQuery);
        String rewritten = miniMaxClient.chat(prompt, 0.1d, 128);
        if (!StringUtils.hasText(rewritten)) {
            return rawQuery;
        }

        String firstLine = rewritten.trim().split("\\r?\\n")[0].trim();
        if (firstLine.length() > 64) {
            firstLine = firstLine.substring(0, 64);
        }
        return StringUtils.hasText(firstLine) ? firstLine : rawQuery;
    }

    private List<PaperSimpleDTO> dataOrEmpty(Result<List<PaperSimpleDTO>> result) {
        if (result == null || CollectionUtils.isEmpty(result.getData())) {
            return Collections.emptyList();
        }
        return result.getData();
    }

    private List<PaperSimpleDTO> rerankByModel(String scene, String trigger, List<PaperSimpleDTO> papers) {
        if (!miniMaxClient.isEnabled() || CollectionUtils.isEmpty(papers) || papers.size() <= 1) {
            return papers;
        }

        Map<Long, PaperSimpleDTO> byId = new LinkedHashMap<>();
        for (PaperSimpleDTO paper : papers) {
            if (paper != null && paper.getId() != null) {
                byId.put(paper.getId(), paper);
            }
        }
        if (byId.size() <= 1) {
            return papers;
        }

        String prompt = buildRerankPrompt(scene, trigger, papers);
        String llmText = miniMaxClient.chat(prompt, 0.2d, 256);
        List<Long> rerankIds = parseIdList(llmText);
        if (CollectionUtils.isEmpty(rerankIds)) {
            return papers;
        }

        List<PaperSimpleDTO> reranked = new ArrayList<>(papers.size());
        for (Long id : rerankIds) {
            PaperSimpleDTO hit = byId.remove(id);
            if (hit != null) {
                reranked.add(hit);
            }
        }
        if (!byId.isEmpty()) {
            reranked.addAll(byId.values());
        }
        return reranked;
    }

    private String buildRerankPrompt(String scene, String trigger, List<PaperSimpleDTO> papers) {
        StringBuilder sb = new StringBuilder();
        for (PaperSimpleDTO paper : papers) {
            if (paper == null || paper.getId() == null) {
                continue;
            }
            sb.append("- ID=").append(paper.getId())
                    .append("; Title=").append(nullToEmpty(paper.getTitle()))
                    .append("; Keywords=").append(nullToEmpty(paper.getKeywords()))
                    .append("; Abstract=").append(nullToEmpty(paper.getAbstractText()))
                    .append('\n');
        }
        return """
                You are ranking academic papers for recommendation.
                Scene: %s
                Trigger: %s
                Candidate papers:
                %s
                Return ONLY a comma-separated list of paper IDs from most relevant to least relevant.
                """.formatted(scene, trigger, sb.toString());
    }

    private List<Long> parseIdList(String text) {
        if (!StringUtils.hasText(text)) {
            return Collections.emptyList();
        }
        Matcher matcher = Pattern.compile("\\d+").matcher(text);
        List<Long> ids = new ArrayList<>();
        while (matcher.find()) {
            try {
                ids.add(Long.parseLong(matcher.group()));
            } catch (Exception ignore) {
                // ignore malformed item
            }
        }
        return ids;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private void saveLog(Long userId, String scene, String trigger, List<PaperSimpleDTO> papers) {
        AiRecommendLogEntity log = new AiRecommendLogEntity();
        log.setUserId(userId);
        log.setScene(scene);
        log.setTriggerText(trigger);
        log.setRecommendItems(toJson(papers));
        log.setCreateTime(LocalDateTime.now());
        recommendLogMapper.insert(log);
    }

    private String toJson(List<PaperSimpleDTO> papers) {
        try {
            return objectMapper.writeValueAsString(papers);
        } catch (JsonProcessingException ex) {
            return "[]";
        }
    }
}
