package cn.academic.cloud.user.domain.dto;

import jakarta.validation.constraints.Size;

public class AdminUserUpdateRequest {

    @Size(max = 64, message = "username length must be <= 64")
    private String username;

    @Size(min = 6, max = 64, message = "password length must be between 6 and 64")
    private String password;

    @Size(max = 64, message = "realName length must be <= 64")
    private String realName;

    @Size(max = 32, message = "phone length must be <= 32")
    private String phone;

    @Size(max = 16, message = "gender length must be <= 16")
    private String gender;

    private Integer status;

    private String role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
