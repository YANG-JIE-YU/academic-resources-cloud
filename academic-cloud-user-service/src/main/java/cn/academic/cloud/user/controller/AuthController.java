package cn.academic.cloud.user.controller;

import cn.academic.cloud.common.constant.CommonConstants;
import cn.academic.cloud.common.core.Result;
import cn.academic.cloud.user.domain.dto.LoginRequest;
import cn.academic.cloud.user.domain.dto.LoginResponse;
import cn.academic.cloud.user.domain.dto.RegisterRequest;
import cn.academic.cloud.user.domain.entity.UserEntity;
import cn.academic.cloud.user.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Result<UserEntity> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success("注册成功", authService.register(request));
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success("登录成功", authService.login(request));
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = CommonConstants.HEADER_AUTHORIZATION, required = false) String authorization) {
        authService.logout(authorization);
        return Result.success();
    }
}
