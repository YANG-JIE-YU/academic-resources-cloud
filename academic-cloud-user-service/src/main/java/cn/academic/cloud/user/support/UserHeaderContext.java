package cn.academic.cloud.user.support;

import cn.academic.cloud.common.constant.CommonConstants;
import cn.academic.cloud.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

public final class UserHeaderContext {

    private UserHeaderContext() {
    }

    public static Long getRequiredUserId(HttpServletRequest request) {
        String value = request.getHeader(CommonConstants.HEADER_USER_ID);
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(401, "缺少用户上下文，请检查网关鉴权");
        }
        return Long.parseLong(value);
    }

    public static String getRole(HttpServletRequest request) {
        return request.getHeader(CommonConstants.HEADER_ROLE);
    }

    public static String getUsername(HttpServletRequest request) {
        return request.getHeader(CommonConstants.HEADER_USERNAME);
    }

    public static boolean isAdmin(HttpServletRequest request) {
        String role = getRole(request);
        return CommonConstants.ROLE_ADMIN.equalsIgnoreCase(role) || "管理员".equals(role);
    }

    public static void requireAdmin(HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(403, "仅管理员可操作");
        }
    }
}
