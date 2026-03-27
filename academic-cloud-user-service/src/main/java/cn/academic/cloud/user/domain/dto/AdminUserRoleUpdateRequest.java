package cn.academic.cloud.user.domain.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminUserRoleUpdateRequest {

    @NotBlank(message = "role must not be blank")
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
