package com.godotvillage.meowkanban.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.godotvillage.meowkanban.common.result.PageResult;
import com.godotvillage.meowkanban.domain.entity.Board;
import com.godotvillage.meowkanban.domain.param.BoardInfoQueryParam;
import com.godotvillage.meowkanban.domain.param.IdParam;
import com.godotvillage.meowkanban.domain.param.NewBoardParam;
import com.godotvillage.meowkanban.domain.vo.BoardDetailVO;
import com.godotvillage.meowkanban.domain.vo.BoardInfoVO;

import java.util.List;

public interface IBoardService extends IService<Board> {

    PageResult<BoardInfoVO> listBoardInfo(BoardInfoQueryParam param);

    List<BoardInfoVO> listRecentParticipatedBoards(IdParam param);

    void newBoard(NewBoardParam param);

	BoardDetailVO getBoardDetail(IdParam param);
}
