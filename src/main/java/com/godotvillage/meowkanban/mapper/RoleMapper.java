package com.godotvillage.meowkanban.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.godotvillage.meowkanban.domain.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    @Select("""
            SELECT r.role_code
            FROM mk_role r
            INNER JOIN mk_user_role ur ON ur.role_id = r.id
            WHERE ur.user_id = #{userId}
              AND ur.deleted = 0
              AND r.deleted = 0
              AND r.status = 'active'
            ORDER BY r.sort_order ASC, r.id ASC
            """)
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);
}
