package cn.academic.cloud.paper.service.impl;

import cn.academic.cloud.common.exception.BusinessException;
import cn.academic.cloud.paper.domain.dto.NoticeSaveRequest;
import cn.academic.cloud.paper.domain.entity.NoticeEntity;
import cn.academic.cloud.paper.mapper.NoticeMapper;
import cn.academic.cloud.paper.service.NoticeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper noticeMapper;

    public NoticeServiceImpl(NoticeMapper noticeMapper) {
        this.noticeMapper = noticeMapper;
    }

    @Override
    public NoticeEntity create(NoticeSaveRequest request) {
        NoticeEntity entity = new NoticeEntity();
        copyFromRequest(entity, request);
        entity.setStatus(normalizeStatus(request.getStatus()));
        entity.setPublishTime(request.getPublishTime() == null ? LocalDateTime.now() : request.getPublishTime());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        noticeMapper.insert(entity);
        return entity;
    }

    @Override
    public NoticeEntity update(Long id, NoticeSaveRequest request) {
        NoticeEntity entity = noticeMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("notice not found");
        }
        copyFromRequest(entity, request);
        entity.setStatus(normalizeStatus(request.getStatus()));
        entity.setPublishTime(request.getPublishTime() == null ? entity.getPublishTime() : request.getPublishTime());
        entity.setUpdateTime(LocalDateTime.now());
        noticeMapper.updateById(entity);
        return entity;
    }

    @Override
    public void delete(Long id) {
        noticeMapper.deleteById(id);
    }

    @Override
    public NoticeEntity detail(Long id, boolean adminView) {
        NoticeEntity entity = noticeMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("notice not found");
        }
        if (!adminView && !Integer.valueOf(1).equals(entity.getStatus())) {
            throw new BusinessException("notice is offline");
        }
        return entity;
    }

    @Override
    public List<NoticeEntity> listPublic(String keyword) {
        LambdaQueryWrapper<NoticeEntity> wrapper = new LambdaQueryWrapper<NoticeEntity>()
                .eq(NoticeEntity::getStatus, 1)
                .orderByDesc(NoticeEntity::getPublishTime)
                .orderByDesc(NoticeEntity::getId);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(NoticeEntity::getTitle, keyword).or().like(NoticeEntity::getSummary, keyword));
        }
        return noticeMapper.selectList(wrapper);
    }

    @Override
    public List<NoticeEntity> listAdmin(String keyword) {
        LambdaQueryWrapper<NoticeEntity> wrapper = new LambdaQueryWrapper<NoticeEntity>()
                .orderByDesc(NoticeEntity::getPublishTime)
                .orderByDesc(NoticeEntity::getId);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(NoticeEntity::getTitle, keyword).or().like(NoticeEntity::getSummary, keyword));
        }
        return noticeMapper.selectList(wrapper);
    }

    private void copyFromRequest(NoticeEntity entity, NoticeSaveRequest request) {
        entity.setTitle(request.getTitle());
        entity.setSummary(request.getSummary());
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
}
