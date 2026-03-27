package cn.academic.cloud.paper.support;

import cn.academic.cloud.common.constant.CommonConstants;
import cn.academic.cloud.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;

public final class UserHeaderContext {

    private UserHeaderContext() {
    }

    public static void requireAdmin(HttpServletRequest request) {
        String role = request.getHeader(CommonConstants.HEADER_ROLE);
        if (!CommonConstants.ROLE_ADMIN.equalsIgnoreCase(role) && !"管理员".equals(role)) {
            throw new BusinessException(403, "仅管理员可操作");
        }
    }
}
