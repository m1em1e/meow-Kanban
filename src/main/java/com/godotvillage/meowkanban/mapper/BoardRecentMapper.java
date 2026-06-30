package com.godotvillage.meowkanban.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.godotvillage.meowkanban.domain.entity.BoardRecent;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface BoardRecentMapper extends BaseMapper<BoardRecent> {

    @Insert("""
            INSERT INTO mk_board_recent (id, board_id, user_id, last_active_time, active_count, created_time, updated_time, deleted)
            VALUES (#{id}, #{boardId}, #{userId}, #{now}, 1, #{now}, #{now}, 0)
            ON CONFLICT(user_id, board_id) DO UPDATE SET
                last_active_time = #{now},
                active_count = COALESCE(mk_board_recent.active_count, 0) + 1,
                deleted = 0,
                deleted_time = NULL,
                updated_time = #{now}
            """)
    void upsertRecent(@Param("id") Long id, @Param("boardId") Long boardId, @Param("userId") Long userId, @Param("now") LocalDateTime now);
}
