package com.godotvillage.meowkanban.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.godotvillage.meowkanban.domain.entity.BoardMember;
import org.apache.ibatis.annotations.Mapper;

/**
 *
 * @author mkdir
 * @since 2026/06/24 10:25
 */
@Mapper
public interface BoardMemberMapper extends BaseMapper<BoardMember> {
}
