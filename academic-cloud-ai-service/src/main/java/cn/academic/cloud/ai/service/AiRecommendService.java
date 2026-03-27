package cn.academic.cloud.ai.service;

import cn.academic.cloud.ai.domain.dto.RecommendResponse;

public interface AiRecommendService {

    RecommendResponse recommend(Long userId, String scene, String query, int limit);
}
