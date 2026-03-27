package cn.academic.cloud.user.service;

import cn.academic.cloud.user.domain.dto.CommentCreateRequest;
import cn.academic.cloud.user.domain.entity.CommentEntity;

import java.util.List;

public interface CommentService {

    CommentEntity createComment(Long userId, CommentCreateRequest request);

    List<CommentEntity> listByTarget(String targetType, Long targetId);

    CommentEntity replyComment(Long commentId, String replyContent);
}
