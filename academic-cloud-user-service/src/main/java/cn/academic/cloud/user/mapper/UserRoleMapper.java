package cn.academic.cloud.user.mapper;

import cn.academic.cloud.user.domain.entity.UserRoleEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

public interface UserRoleMapper extends BaseMapper<UserRoleEntity> {

    @Select("""
            SELECT r.code
            FROM uc_role r
            INNER JOIN uc_user_role ur ON ur.role_id = r.id
            WHERE ur.user_id = #{userId}
            ORDER BY ur.id ASC
            LIMIT 1
            """)
    String findFirstRoleCode(Long userId);
}
