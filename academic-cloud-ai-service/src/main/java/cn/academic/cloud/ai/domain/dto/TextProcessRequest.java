package cn.academic.cloud.ai.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TextProcessRequest {

    @NotBlank(message = "文本不能为空")
    @Size(max = 100000, message = "文本长度不能超过100000")
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
