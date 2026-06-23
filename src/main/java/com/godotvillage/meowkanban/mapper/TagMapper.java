package com.godotvillage.meowkanban.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.godotvillage.meowkanban.domain.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

/**
 *
 * @author mkdir
 * @since 2026/06/23 15:12
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {
}
