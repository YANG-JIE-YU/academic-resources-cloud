package cn.academic.cloud.user.service;

import cn.academic.cloud.user.domain.dto.AdminUserCreateRequest;
import cn.academic.cloud.user.domain.dto.AdminUserUpdateRequest;
import cn.academic.cloud.user.domain.entity.UserEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface UserService {

    UserEntity getById(Long userId);

    IPage<UserEntity> page(int current, int size, String keyword);

    UserEntity createByAdmin(AdminUserCreateRequest request);

    UserEntity updateByAdmin(Long userId, AdminUserUpdateRequest request);

    void deleteByAdmin(Long userId, Long operatorId);

    UserEntity updateStatusByAdmin(Long userId, Integer status);

    UserEntity updateRoleByAdmin(Long userId, String role);
}
