package cn.academic.cloud.ai.service;

import cn.academic.cloud.ai.domain.dto.AdminAiLogItem;

import java.util.List;

public interface AiLogService {

    List<AdminAiLogItem> listLogs(String type, int limit);
}
