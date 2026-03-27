package cn.academic.cloud.paper.service.impl;

import cn.academic.cloud.common.exception.BusinessException;
import cn.academic.cloud.paper.domain.dto.ActivitySaveRequest;
import cn.academic.cloud.paper.domain.entity.ActivityEntity;
import cn.academic.cloud.paper.mapper.ActivityMapper;
import cn.academic.cloud.paper.service.ActivityService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityMapper activityMapper;

    public ActivityServiceImpl(ActivityMapper activityMapper) {
        this.activityMapper = activityMapper;
    }

    @Override
    public ActivityEntity create(ActivitySaveRequest request) {
        validateTime(request.getStartTime(), request.getEndTime());
        ActivityEntity entity = new ActivityEntity();
        copyFromRequest(entity, request);
        entity.setStatus(normalizeStatus(request.getStatus()));
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        activityMapper.insert(entity);
        return entity;
    }

    @Override
    public ActivityEntity update(Long id, ActivitySaveRequest request) {
        ActivityEntity entity = activityMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("activity not found");
        }
        validateTime(request.getStartTime(), request.getEndTime());
        copyFromRequest(entity, request);
        entity.setStatus(normalizeStatus(request.getStatus()));
        entity.setUpdateTime(LocalDateTime.now());
        activityMapper.updateById(entity);
        return entity;
    }

    @Override
    public void delete(Long id) {
        activityMapper.deleteById(id);
    }

    @Override
    public ActivityEntity detail(Long id, boolean adminView) {
        ActivityEntity entity = activityMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("activity not found");
        }
        if (!adminView && !Integer.valueOf(1).equals(entity.getStatus())) {
            throw new BusinessException("activity is offline");
        }
        return entity;
    }

    @Override
    public List<ActivityEntity> listPublic(String keyword) {
        LambdaQueryWrapper<ActivityEntity> wrapper = new LambdaQueryWrapper<ActivityEntity>()
                .eq(ActivityEntity::getStatus, 1)
                .orderByDesc(ActivityEntity::getStartTime)
                .orderByDesc(ActivityEntity::getId);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(ActivityEntity::getTitle, keyword).or().like(ActivityEntity::getOrganizer, keyword));
        }
        return activityMapper.selectList(wrapper);
    }

    @Override
    public List<ActivityEntity> listAdmin(String keyword) {
        LambdaQueryWrapper<ActivityEntity> wrapper = new LambdaQueryWrapper<ActivityEntity>()
                .orderByDesc(ActivityEntity::getStartTime)
                .orderByDesc(ActivityEntity::getId);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(ActivityEntity::getTitle, keyword).or().like(ActivityEntity::getOrganizer, keyword));
        }
        return activityMapper.selectList(wrapper);
    }

    private void copyFromRequest(ActivityEntity entity, ActivitySaveRequest request) {
        entity.setTitle(request.getTitle());
        entity.setLocation(request.getLocation());
        entity.setOrganizer(request.getOrganizer());
        entity.setStartTime(request.getStartTime());
        entity.setEndTime(request.getEndTime());
        entity.setContent(request.getContent());
    }

    private int normalizeStatus(Integer status) {
        if (status == null) {
            return 1;
        }
        if (status != 0 && status != 1) {
            throw new BusinessException("status must be 0 or 1");
        }
        return status;
    }

    private void validateTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new BusinessException("startTime and endTime are required");
        }
        if (endTime.isBefore(startTime)) {
            throw new BusinessException("endTime must be after startTime");
        }
    }
}
