package cn.academic.cloud.user.service;

import cn.academic.cloud.user.domain.dto.LoginRequest;
import cn.academic.cloud.user.domain.dto.LoginResponse;
import cn.academic.cloud.user.domain.dto.RegisterRequest;
import cn.academic.cloud.user.domain.entity.UserEntity;

public interface AuthService {

    UserEntity register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    void logout(String authorizationHeader);
}
