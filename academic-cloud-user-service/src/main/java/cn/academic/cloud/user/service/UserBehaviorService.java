package cn.academic.cloud.user.service;

public interface UserBehaviorService {

    void recordBehavior(Long userId, String behaviorType, String targetType, Long targetId, String metadata);
}
