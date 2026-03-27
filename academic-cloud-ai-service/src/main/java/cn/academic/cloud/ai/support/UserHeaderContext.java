package cn.academic.cloud.ai.support;

import cn.academic.cloud.common.constant.CommonConstants;
import cn.academic.cloud.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

public final class UserHeaderContext {

    private UserHeaderContext() {
    }

    public static Long getOptionalUserId(HttpServletRequest request) {
        String value = request.getHeader(CommonConstants.HEADER_USER_ID);
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return Long.parseLong(value);
    }

    public static Long getRequiredUserId(HttpServletRequest request) {
        Long userId = getOptionalUserId(request);
        if (userId == null) {
            throw new BusinessException(401, "missing user context");
        }
        return userId;
    }

    public static String getRole(HttpServletRequest request) {
        return request.getHeader(CommonConstants.HEADER_ROLE);
    }

    public static boolean isAdmin(HttpServletRequest request) {
        String role = getRole(request);
        return StringUtils.hasText(role) && CommonConstants.ROLE_ADMIN.equalsIgnoreCase(role);
    }

    public static void requireAdmin(HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(403, "admin permission required");
        }
    }
}
