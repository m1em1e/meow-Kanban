package com.godotvillage.meowkanban.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.godotvillage.meowkanban.common.result.PageResult;
import com.godotvillage.meowkanban.domain.entity.Board;
import com.godotvillage.meowkanban.domain.param.BoardInfoQueryParam;
import com.godotvillage.meowkanban.domain.vo.BoardInfo;

public interface IBoardService extends IService<Board> {

    PageResult<BoardInfo> listBoardInfo(BoardInfoQueryParam param);
}
