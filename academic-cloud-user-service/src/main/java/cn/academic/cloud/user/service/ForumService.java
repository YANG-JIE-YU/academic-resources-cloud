package cn.academic.cloud.user.service;

import cn.academic.cloud.user.domain.dto.ForumPostCreateRequest;
import cn.academic.cloud.user.domain.dto.ForumReplyCreateRequest;
import cn.academic.cloud.user.domain.entity.ForumPostEntity;

import java.util.List;

public interface ForumService {

    ForumPostEntity createPost(Long userId, ForumPostCreateRequest request);

    ForumPostEntity replyPost(Long userId, ForumReplyCreateRequest request);

    List<ForumPostEntity> listRootPosts();

    List<ForumPostEntity> listReplies(Long parentId);
}
