package com.godotvillage.meowkanban.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.godotvillage.meowkanban.domain.entity.BoardFavorite;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface BoardFavoriteMapper extends BaseMapper<BoardFavorite> {

    @Insert("""
            INSERT INTO mk_board_favorite (id, board_id, user_id, created_time, updated_time, deleted)
            VALUES (#{id}, #{boardId}, #{userId}, #{now}, #{now}, 0)
            ON CONFLICT(user_id, board_id) DO UPDATE SET
                deleted = 0,
                deleted_time = NULL,
                updated_time = #{now}
            """)
    void upsertFavorite(@Param("id") Long id, @Param("boardId") Long boardId, @Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Update("""
            UPDATE mk_board_favorite
            SET deleted = 1,
                deleted_time = #{now},
                updated_time = #{now}
            WHERE board_id = #{boardId}
              AND user_id = #{userId}
              AND deleted = 0
            """)
    void cancelFavorite(@Param("boardId") Long boardId, @Param("userId") Long userId, @Param("now") LocalDateTime now);
}
