package com.godotvillage.meowkanban.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.godotvillage.meowkanban.domain.entity.BoardSection;
import org.apache.ibatis.annotations.Mapper;

/**
 *
 * @author mkdir
 * @since 2026/06/23 13:35
 */
@Mapper
public interface BoardSectionMapper extends BaseMapper<BoardSection> {
}
