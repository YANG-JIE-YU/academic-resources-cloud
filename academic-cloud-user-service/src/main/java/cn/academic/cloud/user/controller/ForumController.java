package cn.academic.cloud.user.controller;

import cn.academic.cloud.common.core.Result;
import cn.academic.cloud.user.domain.dto.ForumPostCreateRequest;
import cn.academic.cloud.user.domain.dto.ForumReplyCreateRequest;
import cn.academic.cloud.user.domain.entity.ForumPostEntity;
import cn.academic.cloud.user.service.ForumService;
import cn.academic.cloud.user.support.UserHeaderContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/forum")
public class ForumController {

    private final ForumService forumService;

    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    @PostMapping("/posts")
    public Result<ForumPostEntity> createPost(@Valid @RequestBody ForumPostCreateRequest request, HttpServletRequest httpRequest) {
        Long userId = UserHeaderContext.getRequiredUserId(httpRequest);
        return Result.success("发帖成功", forumService.createPost(userId, request));
    }

    @PostMapping("/replies")
    public Result<ForumPostEntity> reply(@Valid @RequestBody ForumReplyCreateRequest request, HttpServletRequest httpRequest) {
        Long userId = UserHeaderContext.getRequiredUserId(httpRequest);
        return Result.success("回复成功", forumService.replyPost(userId, request));
    }

    @GetMapping("/posts")
    public Result<List<ForumPostEntity>> listPosts() {
        return Result.success(forumService.listRootPosts());
    }

    @GetMapping("/posts/{parentId}/replies")
    public Result<List<ForumPostEntity>> listReplies(@PathVariable("parentId") Long parentId) {
        return Result.success(forumService.listReplies(parentId));
    }
}
