package cn.academic.cloud.user.controller;

import cn.academic.cloud.common.core.Result;
import cn.academic.cloud.user.domain.dto.AdminUserCreateRequest;
import cn.academic.cloud.user.domain.dto.AdminUserRoleUpdateRequest;
import cn.academic.cloud.user.domain.dto.AdminUserStatusUpdateRequest;
import cn.academic.cloud.user.domain.dto.AdminUserUpdateRequest;
import cn.academic.cloud.user.domain.entity.UserEntity;
import cn.academic.cloud.user.service.UserService;
import cn.academic.cloud.user.support.UserHeaderContext;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public Result<UserEntity> currentUser(HttpServletRequest request) {
        Long userId = UserHeaderContext.getRequiredUserId(request);
        return Result.success(userService.getById(userId));
    }

    @GetMapping("/admin/page")
    public Result<IPage<UserEntity>> page(@RequestParam(value = "current", defaultValue = "1") int current,
                                          @RequestParam(value = "size", defaultValue = "10") int size,
                                          @RequestParam(value = "keyword", required = false) String keyword,
                                          HttpServletRequest request) {
        UserHeaderContext.requireAdmin(request);
        return Result.success(userService.page(current, size, keyword));
    }

    @PostMapping("/admin")
    public Result<UserEntity> create(@Valid @RequestBody AdminUserCreateRequest request, HttpServletRequest httpRequest) {
        UserHeaderContext.requireAdmin(httpRequest);
        return Result.success("user created", userService.createByAdmin(request));
    }

    @PutMapping("/admin/{userId}")
    public Result<UserEntity> update(@PathVariable("userId") Long userId,
                                     @Valid @RequestBody AdminUserUpdateRequest request,
                                     HttpServletRequest httpRequest) {
        UserHeaderContext.requireAdmin(httpRequest);
        return Result.success("user updated", userService.updateByAdmin(userId, request));
    }

    @DeleteMapping("/admin/{userId}")
    public Result<Void> delete(@PathVariable("userId") Long userId, HttpServletRequest httpRequest) {
        UserHeaderContext.requireAdmin(httpRequest);
        Long operatorId = UserHeaderContext.getRequiredUserId(httpRequest);
        userService.deleteByAdmin(userId, operatorId);
        return Result.success();
    }

    @PutMapping("/admin/{userId}/status")
    public Result<UserEntity> updateStatus(@PathVariable("userId") Long userId,
                                           @Valid @RequestBody AdminUserStatusUpdateRequest request,
                                           HttpServletRequest httpRequest) {
        UserHeaderContext.requireAdmin(httpRequest);
        return Result.success("status updated", userService.updateStatusByAdmin(userId, request.getStatus()));
    }

    @PutMapping("/admin/{userId}/role")
    public Result<UserEntity> updateRole(@PathVariable("userId") Long userId,
                                         @Valid @RequestBody AdminUserRoleUpdateRequest request,
                                         HttpServletRequest httpRequest) {
        UserHeaderContext.requireAdmin(httpRequest);
        return Result.success("role updated", userService.updateRoleByAdmin(userId, request.getRole()));
    }
}
