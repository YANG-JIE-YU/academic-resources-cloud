package cn.academic.cloud.user.service.impl;

import cn.academic.cloud.user.domain.entity.UserBehaviorEntity;
import cn.academic.cloud.user.mapper.UserBehaviorMapper;
import cn.academic.cloud.user.service.UserBehaviorService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserBehaviorServiceImpl implements UserBehaviorService {

    private final UserBehaviorMapper userBehaviorMapper;

    public UserBehaviorServiceImpl(UserBehaviorMapper userBehaviorMapper) {
        this.userBehaviorMapper = userBehaviorMapper;
    }

    @Override
    public void recordBehavior(Long userId, String behaviorType, String targetType, Long targetId, String metadata) {
        UserBehaviorEntity entity = new UserBehaviorEntity();
        entity.setUserId(userId);
        entity.setBehaviorType(behaviorType);
        entity.setTargetType(targetType);
        entity.setTargetId(targetId);
        entity.setMetadata(metadata);
        entity.setCreateTime(LocalDateTime.now());
        userBehaviorMapper.insert(entity);
    }
}
