package cn.academic.cloud.ai.service.impl;

import cn.academic.cloud.ai.domain.dto.AdminAiLogItem;
import cn.academic.cloud.ai.domain.entity.AiConversationLogEntity;
import cn.academic.cloud.ai.domain.entity.AiPaperTaskLogEntity;
import cn.academic.cloud.ai.domain.entity.AiRecommendLogEntity;
import cn.academic.cloud.ai.mapper.AiConversationLogMapper;
import cn.academic.cloud.ai.mapper.AiPaperTaskLogMapper;
import cn.academic.cloud.ai.mapper.AiRecommendLogMapper;
import cn.academic.cloud.ai.service.AiLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class AiLogServiceImpl implements AiLogService {

    private final AiConversationLogMapper conversationLogMapper;
    private final AiPaperTaskLogMapper paperTaskLogMapper;
    private final AiRecommendLogMapper recommendLogMapper;

    public AiLogServiceImpl(AiConversationLogMapper conversationLogMapper,
                            AiPaperTaskLogMapper paperTaskLogMapper,
                            AiRecommendLogMapper recommendLogMapper) {
        this.conversationLogMapper = conversationLogMapper;
        this.paperTaskLogMapper = paperTaskLogMapper;
        this.recommendLogMapper = recommendLogMapper;
    }

    @Override
    public List<AdminAiLogItem> listLogs(String type, int limit) {
        int validLimit = Math.min(Math.max(limit, 1), 200);
        String normalized = normalizeType(type);

        if ("CHAT".equals(normalized)) {
            return queryChat(validLimit);
        }
        if ("SUMMARY".equals(normalized) || "KEYWORD".equals(normalized)) {
            return queryPaperTask(normalized, validLimit);
        }
        if ("RECOMMEND".equals(normalized)) {
            return queryRecommend(validLimit);
        }

        List<AdminAiLogItem> merged = new ArrayList<>();
        merged.addAll(queryChat(validLimit));
        merged.addAll(queryPaperTask(null, validLimit));
        merged.addAll(queryRecommend(validLimit));
        merged.sort(Comparator.comparing(AdminAiLogItem::getCreateTime, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        if (merged.size() <= validLimit) {
            return merged;
        }
        return merged.subList(0, validLimit);
    }

    private List<AdminAiLogItem> queryChat(int limit) {
        List<AiConversationLogEntity> list = conversationLogMapper.selectList(new LambdaQueryWrapper<AiConversationLogEntity>()
                .orderByDesc(AiConversationLogEntity::getCreateTime)
                .last("LIMIT " + limit));
        List<AdminAiLogItem> result = new ArrayList<>(list.size());
        for (AiConversationLogEntity item : list) {
            AdminAiLogItem dto = new AdminAiLogItem();
            dto.setId(item.getId());
            dto.setUserId(item.getUserId());
            dto.setLogType("CHAT");
            dto.setTriggerText(abbreviate(item.getQuestion(), 120));
            dto.setCreateTime(item.getCreateTime());
            result.add(dto);
        }
        return result;
    }

    private List<AdminAiLogItem> queryPaperTask(String taskType, int limit) {
        LambdaQueryWrapper<AiPaperTaskLogEntity> wrapper = new LambdaQueryWrapper<AiPaperTaskLogEntity>()
                .orderByDesc(AiPaperTaskLogEntity::getCreateTime)
                .last("LIMIT " + limit);
        if (StringUtils.hasText(taskType)) {
            wrapper.eq(AiPaperTaskLogEntity::getTaskType, taskType);
        }
        List<AiPaperTaskLogEntity> list = paperTaskLogMapper.selectList(wrapper);
        List<AdminAiLogItem> result = new ArrayList<>(list.size());
        for (AiPaperTaskLogEntity item : list) {
            AdminAiLogItem dto = new AdminAiLogItem();
            dto.setId(item.getId());
            dto.setUserId(item.getUserId());
            dto.setLogType(item.getTaskType());
            dto.setTriggerText(abbreviate(item.getInputText(), 120));
            dto.setCreateTime(item.getCreateTime());
            result.add(dto);
        }
        return result;
    }

    private List<AdminAiLogItem> queryRecommend(int limit) {
        List<AiRecommendLogEntity> list = recommendLogMapper.selectList(new LambdaQueryWrapper<AiRecommendLogEntity>()
                .orderByDesc(AiRecommendLogEntity::getCreateTime)
                .last("LIMIT " + limit));
        List<AdminAiLogItem> result = new ArrayList<>(list.size());
        for (AiRecommendLogEntity item : list) {
            AdminAiLogItem dto = new AdminAiLogItem();
            dto.setId(item.getId());
            dto.setUserId(item.getUserId());
            dto.setLogType("RECOMMEND");
            dto.setTriggerText(abbreviate(item.getTriggerText(), 120));
            dto.setCreateTime(item.getCreateTime());
            result.add(dto);
        }
        return result;
    }

    private String normalizeType(String type) {
        if (!StringUtils.hasText(type)) {
            return "ALL";
        }
        String upper = type.trim().toUpperCase(Locale.ROOT);
        return switch (upper) {
            case "CHAT", "SUMMARY", "KEYWORD", "RECOMMEND" -> upper;
            default -> "ALL";
        };
    }

    private String abbreviate(String text, int max) {
        if (!StringUtils.hasText(text)) {
            return "";
        }
        if (text.length() <= max) {
            return text;
        }
        return text.substring(0, max) + "...";
    }
}
