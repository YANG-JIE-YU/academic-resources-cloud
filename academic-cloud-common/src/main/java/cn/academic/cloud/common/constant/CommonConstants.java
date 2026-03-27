package cn.academic.cloud.common.constant;

/**
 * Common constants shared by all services.
 */
public final class CommonConstants {

    private CommonConstants() {
    }

    public static final Integer SUCCESS_CODE = 0;
    public static final Integer ERROR_CODE = 500;

    public static final String DEFAULT_SUCCESS_MSG = "success";
    public static final String DEFAULT_ERROR_MSG = "error";

    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USERNAME = "X-Username";
    public static final String HEADER_ROLE = "X-Role";
    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_USERNAME = "username";
    public static final String CLAIM_ROLE = "role";

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";

    public static final long DEFAULT_TOKEN_EXPIRE_SECONDS = 24 * 60 * 60L;
}
