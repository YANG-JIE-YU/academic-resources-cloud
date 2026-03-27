package cn.academic.cloud.user.service.impl;

import cn.academic.cloud.common.constant.CommonConstants;
import cn.academic.cloud.user.domain.dto.AdminUserCreateRequest;
import cn.academic.cloud.user.domain.dto.AdminUserUpdateRequest;
import cn.academic.cloud.user.domain.entity.RoleEntity;
import cn.academic.cloud.user.domain.entity.UserEntity;
import cn.academic.cloud.user.domain.entity.UserRoleEntity;
import cn.academic.cloud.user.mapper.RoleMapper;
import cn.academic.cloud.user.mapper.UserMapper;
import cn.academic.cloud.user.mapper.UserRoleMapper;
import cn.academic.cloud.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserMapper userMapper, UserRoleMapper userRoleMapper, RoleMapper roleMapper) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public UserEntity getById(Long userId) {
        return sanitizeAndFillRole(userMapper.selectById(userId));
    }

    @Override
    public IPage<UserEntity> page(int current, int size, String keyword) {
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(UserEntity::getUsername, keyword).or().like(UserEntity::getRealName, keyword);
        }
        wrapper.orderByDesc(UserEntity::getCreateTime);
        IPage<UserEntity> result = userMapper.selectPage(new Page<>(current, size), wrapper);
        result.getRecords().forEach(this::sanitizeAndFillRole);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserEntity createByAdmin(AdminUserCreateRequest request) {
        validateStatus(request.getStatus());
        checkUsernameAvailable(request.getUsername(), null);

        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());
        user.setStatus(request.getStatus());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);

        bindUserRole(user.getId(), request.getRole());
        return getById(user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserEntity updateByAdmin(Long userId, AdminUserUpdateRequest request) {
        UserEntity user = requireUser(userId);

        if (StringUtils.hasText(request.getUsername())) {
            String username = request.getUsername().trim();
            if (!username.equals(user.getUsername())) {
                checkUsernameAvailable(username, userId);
                user.setUsername(username);
            }
        }
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());

        if (request.getStatus() != null) {
            validateStatus(request.getStatus());
            user.setStatus(request.getStatus());
        }

        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        if (StringUtils.hasText(request.getRole())) {
            bindUserRole(userId, request.getRole());
        }

        return getById(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByAdmin(Long userId, Long operatorId) {
        if (operatorId != null && operatorId.equals(userId)) {
            throw new IllegalArgumentException("current admin account cannot be deleted");
        }
        requireUser(userId);

        userRoleMapper.delete(new LambdaQueryWrapper<UserRoleEntity>()
                .eq(UserRoleEntity::getUserId, userId));
        userMapper.deleteById(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserEntity updateStatusByAdmin(Long userId, Integer status) {
        validateStatus(status);
        UserEntity user = requireUser(userId);
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        return getById(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserEntity updateRoleByAdmin(Long userId, String role) {
        requireUser(userId);
        bindUserRole(userId, role);
        return getById(userId);
    }

    private UserEntity requireUser(Long userId) {
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("user not found");
        }
        return user;
    }

    private void checkUsernameAvailable(String username, Long excludedUserId) {
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, username.trim());
        if (excludedUserId != null) {
            wrapper.ne(UserEntity::getId, excludedUserId);
        }
        if (userMapper.selectCount(wrapper) > 0) {
            throw new IllegalArgumentException("username already exists");
        }
    }

    private void validateStatus(Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new IllegalArgumentException("status must be 0 or 1");
        }
    }

    private void bindUserRole(Long userId, String roleText) {
        String roleCode = normalizeRoleCode(roleText);
        RoleEntity role = ensureRole(roleCode);

        userRoleMapper.delete(new LambdaQueryWrapper<UserRoleEntity>()
                .eq(UserRoleEntity::getUserId, userId));

        UserRoleEntity relation = new UserRoleEntity();
        relation.setUserId(userId);
        relation.setRoleId(role.getId());
        relation.setCreateTime(LocalDateTime.now());
        userRoleMapper.insert(relation);
    }

    private RoleEntity ensureRole(String roleCode) {
        LambdaQueryWrapper<RoleEntity> wrapper = new LambdaQueryWrapper<RoleEntity>()
                .eq(RoleEntity::getCode, roleCode)
                .last("LIMIT 1");
        RoleEntity role = roleMapper.selectOne(wrapper);
        if (role != null) {
            return role;
        }

        RoleEntity entity = new RoleEntity();
        entity.setCode(roleCode);
        entity.setName(CommonConstants.ROLE_ADMIN.equals(roleCode) ? "Administrator" : "User");
        entity.setCreateTime(LocalDateTime.now());
        roleMapper.insert(entity);
        return entity;
    }

    private String normalizeRoleCode(String rawRole) {
        if (!StringUtils.hasText(rawRole)) {
            return CommonConstants.ROLE_USER;
        }

        String upper = rawRole.trim().toUpperCase();
        if (CommonConstants.ROLE_ADMIN.equals(upper) || "ROLE_ADMIN".equals(upper) || "SUPER_ADMIN".equals(upper)) {
            return CommonConstants.ROLE_ADMIN;
        }
        if (CommonConstants.ROLE_USER.equals(upper) || "ROLE_USER".equals(upper)) {
            return CommonConstants.ROLE_USER;
        }

        throw new IllegalArgumentException("unsupported role: " + rawRole);
    }

    private UserEntity sanitizeAndFillRole(UserEntity user) {
        if (user == null) {
            return null;
        }
        user.setPassword(null);
        String role = userRoleMapper.findFirstRoleCode(user.getId());
        if (!StringUtils.hasText(role)) {
            role = CommonConstants.ROLE_USER;
        }
        user.setRole(role);
        return user;
    }
}
