package com.godotvillage.meowkanban.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.godotvillage.meowkanban.domain.entity.BoardRecent;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoardRecentMapper extends BaseMapper<BoardRecent> {
}
