package com.godotvillage.meowkanban.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.godotvillage.meowkanban.domain.entity.BoardFavorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoardFavoriteMapper extends BaseMapper<BoardFavorite> {
}
