package cn.academic.cloud.user.service.impl;

import cn.academic.cloud.common.constant.CommonConstants;
import cn.academic.cloud.common.util.JwtUtils;
import cn.academic.cloud.user.config.UserJwtProperties;
import cn.academic.cloud.user.domain.dto.LoginRequest;
import cn.academic.cloud.user.domain.dto.LoginResponse;
import cn.academic.cloud.user.domain.dto.RegisterRequest;
import cn.academic.cloud.user.domain.entity.RoleEntity;
import cn.academic.cloud.user.domain.entity.UserEntity;
import cn.academic.cloud.user.domain.entity.UserRoleEntity;
import cn.academic.cloud.user.mapper.RoleMapper;
import cn.academic.cloud.user.mapper.UserMapper;
import cn.academic.cloud.user.mapper.UserRoleMapper;
import cn.academic.cloud.user.service.AuthService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final UserJwtProperties jwtProperties;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthServiceImpl(UserMapper userMapper, RoleMapper roleMapper, UserRoleMapper userRoleMapper, UserJwtProperties jwtProperties) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
        this.jwtProperties = jwtProperties;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserEntity register(RegisterRequest request) {
        LambdaQueryWrapper<UserEntity> existsWrapper = new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, request.getUsername());
        UserEntity exists = userMapper.selectOne(existsWrapper);
        if (exists != null) {
            throw new IllegalArgumentException("用户名已存在");
        }

        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);

        RoleEntity userRole = ensureDefaultUserRole();
        UserRoleEntity relation = new UserRoleEntity();
        relation.setUserId(user.getId());
        relation.setRoleId(userRole.getId());
        relation.setCreateTime(LocalDateTime.now());
        userRoleMapper.insert(relation);
        return user;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, request.getUsername())
                .last("LIMIT 1");
        UserEntity user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new IllegalArgumentException("账号不存在");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("账号或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new IllegalArgumentException("账号已禁用");
        }

        String role = userRoleMapper.findFirstRoleCode(user.getId());
        if (!StringUtils.hasText(role)) {
            role = CommonConstants.ROLE_USER;
        }

        String token = JwtUtils.createToken(
                user.getId(),
                user.getUsername(),
                role,
                jwtProperties.getSecret(),
                jwtProperties.getExpireSeconds()
        );

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setTokenType(CommonConstants.TOKEN_PREFIX.trim());
        response.setExpiresIn(jwtProperties.getExpireSeconds());
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRole(role);
        return response;
    }

    @Override
    public void logout(String authorizationHeader) {
        // 当前版本使用无状态JWT，网关验签即可。
        // 后续若接入Redis黑名单，可在此处记录token吊销状态。
    }

    private RoleEntity ensureDefaultUserRole() {
        LambdaQueryWrapper<RoleEntity> wrapper = new LambdaQueryWrapper<RoleEntity>()
                .eq(RoleEntity::getCode, CommonConstants.ROLE_USER)
                .last("LIMIT 1");
        RoleEntity role = roleMapper.selectOne(wrapper);
        if (role != null) {
            return role;
        }

        RoleEntity entity = new RoleEntity();
        entity.setCode(CommonConstants.ROLE_USER);
        entity.setName("普通用户");
        entity.setCreateTime(LocalDateTime.now());
        roleMapper.insert(entity);
        return entity;
    }
}
