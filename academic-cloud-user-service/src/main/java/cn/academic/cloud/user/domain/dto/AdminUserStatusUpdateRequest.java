package cn.academic.cloud.user.domain.dto;

import jakarta.validation.constraints.NotNull;

public class AdminUserStatusUpdateRequest {

    @NotNull(message = "status must not be null")
    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
