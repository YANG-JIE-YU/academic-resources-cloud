package cn.academic.cloud.user.controller;

import cn.academic.cloud.common.core.Result;
import cn.academic.cloud.user.domain.dto.CommentCreateRequest;
import cn.academic.cloud.user.domain.dto.CommentReplyRequest;
import cn.academic.cloud.user.domain.entity.CommentEntity;
import cn.academic.cloud.user.service.CommentService;
import cn.academic.cloud.user.support.UserHeaderContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public Result<CommentEntity> create(@Valid @RequestBody CommentCreateRequest request, HttpServletRequest httpRequest) {
        Long userId = UserHeaderContext.getRequiredUserId(httpRequest);
        return Result.success("comment created", commentService.createComment(userId, request));
    }

    @GetMapping
    public Result<List<CommentEntity>> list(@RequestParam("targetType") String targetType,
                                            @RequestParam("targetId") Long targetId) {
        return Result.success(commentService.listByTarget(targetType, targetId));
    }

    @PutMapping("/admin/{commentId}/reply")
    public Result<CommentEntity> reply(@PathVariable("commentId") Long commentId,
                                       @Valid @RequestBody CommentReplyRequest request,
                                       HttpServletRequest httpRequest) {
        UserHeaderContext.requireAdmin(httpRequest);
        return Result.success("comment replied", commentService.replyComment(commentId, request.getReplyContent()));
    }
}
