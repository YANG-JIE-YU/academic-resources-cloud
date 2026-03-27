package cn.academic.cloud.user.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentReplyRequest {

    @NotBlank(message = "replyContent must not be blank")
    @Size(max = 2000, message = "replyContent length must be <= 2000")
    private String replyContent;

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }
}
