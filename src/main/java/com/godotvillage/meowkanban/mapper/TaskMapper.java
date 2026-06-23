package com.godotvillage.meowkanban.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.godotvillage.meowkanban.domain.entity.Task;
import org.apache.ibatis.annotations.Mapper;

/**
 *
 * @author mkdir
 * @since 2026/06/23 13:44
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {
}
