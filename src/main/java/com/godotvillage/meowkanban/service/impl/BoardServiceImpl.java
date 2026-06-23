package com.godotvillage.meowkanban.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.godotvillage.meowkanban.common.exception.BaseException;
import com.godotvillage.meowkanban.common.result.PageResult;
import com.godotvillage.meowkanban.domain.entity.Board;
import com.godotvillage.meowkanban.domain.param.BoardInfoQueryParam;
import com.godotvillage.meowkanban.domain.param.IdParam;
import com.godotvillage.meowkanban.domain.param.NewBoardParam;
import com.godotvillage.meowkanban.domain.vo.BoardDetailVO;
import com.godotvillage.meowkanban.domain.vo.BoardInfoVO;
import com.godotvillage.meowkanban.mapper.BoardMapper;
import com.godotvillage.meowkanban.service.IBoardService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class BoardServiceImpl extends ServiceImpl<BoardMapper, Board> implements IBoardService {

    @Override
    public PageResult<BoardInfoVO> listBoardInfo(BoardInfoQueryParam param) {
        if (param == null) {
            param = new BoardInfoQueryParam();
        }

        Page<Board> page = new Page<>(param.getPageIndex(), param.getPageSize());
        IPage<Board> boardPage = baseMapper.getBoardInfoList(page, param);
        List<BoardInfoVO> records = boardPage.getRecords().stream()
                .map(this::toBoardInfo)
                .toList();
        return PageResult.of(
                records,
                boardPage.getTotal(),
                boardPage.getCurrent(),
                boardPage.getSize(),
                boardPage.getPages()
        );
    }

    @Override
    public List<BoardInfoVO> listRecentParticipatedBoards(IdParam param) {
        if (param == null || param.getId() == null) {
            throw new BaseException("用户 ID 不能为空");
        }

        return baseMapper.getRecentParticipatedBoards(param.getId())
                .stream()
                .map(this::toBoardInfo)
                .toList();
    }

    private BoardInfoVO toBoardInfo(Board board) {
        BoardInfoVO boardInfo = new BoardInfoVO();
        boardInfo.setId(board.getId());
        boardInfo.setName(board.getName());
        boardInfo.setDescription(board.getDescription());
        boardInfo.setCoverResourceId(board.getCoverResourceId());
        boardInfo.setOwnerId(board.getOwnerId());
        boardInfo.setVisibility(board.getVisibility());
        return boardInfo;
    }

    @Override
    @Transactional
    public void newBoard(NewBoardParam param) {
        if (param == null || !StringUtils.hasText(param.getName())) {
            throw new BaseException("看板名称不能为空");
        }

        Board board = new Board();
        BeanUtils.copyProperties(param, board);
        board.setName(param.getName().trim());
        board.setDescription(StringUtils.hasText(param.getDescription()) ? param.getDescription().trim() : null);
        board.setOwnerId(param.getUserId());
        board.setVisibility(param.getVisibility() == null ? 0 : param.getVisibility());
        board.setCoverResourceId(param.getCoverResourceId());
        baseMapper.insert(board);
    }

	@Override
	public BoardDetailVO getBoardDetail(IdParam param) {
		if (param == null || param.getId() == null) {
			throw new BaseException("看板 ID 不能为空");
		}

		BoardDetailVO boardDetailVO = baseMapper.getBoardDetail(param.getId());
		if (boardDetailVO == null) {
			throw new BaseException("看板不存在");
		}
		return boardDetailVO;
	}
}
