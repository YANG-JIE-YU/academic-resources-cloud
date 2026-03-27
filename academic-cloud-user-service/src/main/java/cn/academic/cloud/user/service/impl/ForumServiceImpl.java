package cn.academic.cloud.user.service.impl;

import cn.academic.cloud.common.exception.BusinessException;
import cn.academic.cloud.user.domain.dto.ForumPostCreateRequest;
import cn.academic.cloud.user.domain.dto.ForumReplyCreateRequest;
import cn.academic.cloud.user.domain.entity.ForumPostEntity;
import cn.academic.cloud.user.mapper.ForumPostMapper;
import cn.academic.cloud.user.service.ForumService;
import cn.academic.cloud.user.service.UserBehaviorService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ForumServiceImpl implements ForumService {

    private final ForumPostMapper forumPostMapper;
    private final UserBehaviorService userBehaviorService;

    public ForumServiceImpl(ForumPostMapper forumPostMapper, UserBehaviorService userBehaviorService) {
        this.forumPostMapper = forumPostMapper;
        this.userBehaviorService = userBehaviorService;
    }

    @Override
    public ForumPostEntity createPost(Long userId, ForumPostCreateRequest request) {
        ForumPostEntity entity = new ForumPostEntity();
        entity.setParentId(0L);
        entity.setUserId(userId);
        entity.setTitle(request.getTitle());
        entity.setContent(request.getContent());
        entity.setIsTop(0);
        entity.setStatus("OPEN");
        entity.setCreateTime(LocalDateTime.now());
        forumPostMapper.insert(entity);

        userBehaviorService.recordBehavior(userId, "FORUM_POST", "forum", entity.getId(), request.getTitle());
        return entity;
    }

    @Override
    public ForumPostEntity replyPost(Long userId, ForumReplyCreateRequest request) {
        ForumPostEntity parent = forumPostMapper.selectById(request.getParentId());
        if (parent == null) {
            throw new BusinessException("父帖子不存在");
        }

        ForumPostEntity entity = new ForumPostEntity();
        entity.setParentId(request.getParentId());
        entity.setUserId(userId);
        entity.setTitle("RE:" + (parent.getTitle() == null ? "帖子" : parent.getTitle()));
        entity.setContent(request.getContent());
        entity.setIsTop(0);
        entity.setStatus("OPEN");
        entity.setCreateTime(LocalDateTime.now());
        forumPostMapper.insert(entity);

        userBehaviorService.recordBehavior(userId, "FORUM_REPLY", "forum", request.getParentId(), request.getContent());
        return entity;
    }

    @Override
    public List<ForumPostEntity> listRootPosts() {
        LambdaQueryWrapper<ForumPostEntity> wrapper = new LambdaQueryWrapper<ForumPostEntity>()
                .eq(ForumPostEntity::getParentId, 0L)
                .orderByDesc(ForumPostEntity::getIsTop)
                .orderByDesc(ForumPostEntity::getCreateTime);
        return forumPostMapper.selectList(wrapper);
    }

    @Override
    public List<ForumPostEntity> listReplies(Long parentId) {
        LambdaQueryWrapper<ForumPostEntity> wrapper = new LambdaQueryWrapper<ForumPostEntity>()
                .eq(ForumPostEntity::getParentId, parentId)
                .orderByAsc(ForumPostEntity::getCreateTime);
        return forumPostMapper.selectList(wrapper);
    }
}
