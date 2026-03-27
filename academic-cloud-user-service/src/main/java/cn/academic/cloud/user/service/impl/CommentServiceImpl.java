package cn.academic.cloud.user.service.impl;

import cn.academic.cloud.user.domain.dto.CommentCreateRequest;
import cn.academic.cloud.user.domain.entity.CommentEntity;
import cn.academic.cloud.user.domain.entity.UserEntity;
import cn.academic.cloud.user.mapper.CommentMapper;
import cn.academic.cloud.user.mapper.UserMapper;
import cn.academic.cloud.user.service.CommentService;
import cn.academic.cloud.user.service.UserBehaviorService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final UserMapper userMapper;
    private final UserBehaviorService userBehaviorService;

    public CommentServiceImpl(CommentMapper commentMapper, UserMapper userMapper, UserBehaviorService userBehaviorService) {
        this.commentMapper = commentMapper;
        this.userMapper = userMapper;
        this.userBehaviorService = userBehaviorService;
    }

    @Override
    public CommentEntity createComment(Long userId, CommentCreateRequest request) {
        CommentEntity entity = new CommentEntity();
        entity.setUserId(userId);
        entity.setTargetType(request.getTargetType());
        entity.setTargetId(request.getTargetId());
        entity.setContent(request.getContent());
        entity.setCreateTime(LocalDateTime.now());
        commentMapper.insert(entity);

        userBehaviorService.recordBehavior(userId, "COMMENT", request.getTargetType(), request.getTargetId(), request.getContent());
        return entity;
    }

    @Override
    public List<CommentEntity> listByTarget(String targetType, Long targetId) {
        LambdaQueryWrapper<CommentEntity> wrapper = new LambdaQueryWrapper<CommentEntity>()
                .eq(CommentEntity::getTargetType, targetType)
                .eq(CommentEntity::getTargetId, targetId)
                .orderByDesc(CommentEntity::getCreateTime);
        List<CommentEntity> comments = commentMapper.selectList(wrapper);
        fillUsername(comments);
        return comments;
    }

    @Override
    public CommentEntity replyComment(Long commentId, String replyContent) {
        CommentEntity comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new IllegalArgumentException("评论不存在");
        }
        comment.setReplyContent(replyContent);
        commentMapper.updateById(comment);
        return comment;
    }

    private void fillUsername(List<CommentEntity> comments) {
        if (CollectionUtils.isEmpty(comments)) {
            return;
        }

        Set<Long> userIds = comments.stream()
                .map(CommentEntity::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }

        List<UserEntity> users = userMapper.selectList(new LambdaQueryWrapper<UserEntity>()
                .select(UserEntity::getId, UserEntity::getUsername)
                .in(UserEntity::getId, userIds));
        Map<Long, String> usernameByUserId = new HashMap<>();
        for (UserEntity user : users) {
            usernameByUserId.put(user.getId(), user.getUsername());
        }
        for (CommentEntity comment : comments) {
            comment.setUsername(usernameByUserId.get(comment.getUserId()));
        }
    }
}
