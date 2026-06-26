package com.godotvillage.meowkanban.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.godotvillage.meowkanban.domain.entity.TaskComment;
import org.apache.ibatis.annotations.Mapper;

/**
 *
 * @author mkdir
 * @since 2026/06/26 16:28
 */
@Mapper
public interface TaskCommentMapper extends BaseMapper<TaskComment> {
}
