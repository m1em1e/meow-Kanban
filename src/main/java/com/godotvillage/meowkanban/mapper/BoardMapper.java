package com.godotvillage.meowkanban.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.godotvillage.meowkanban.domain.entity.Board;
import com.godotvillage.meowkanban.domain.param.BoardInfoQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BoardMapper extends BaseMapper<Board> {

    IPage<Board> getBoardInfoList(Page<Board> page, @Param("param") BoardInfoQueryParam param);

}
