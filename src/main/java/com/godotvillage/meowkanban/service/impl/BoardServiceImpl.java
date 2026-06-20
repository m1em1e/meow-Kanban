package com.godotvillage.meowkanban.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.godotvillage.meowkanban.common.exception.BaseException;
import com.godotvillage.meowkanban.common.result.PageResult;
import com.godotvillage.meowkanban.domain.entity.Board;
import com.godotvillage.meowkanban.domain.param.BoardInfoQueryParam;
import com.godotvillage.meowkanban.domain.param.IdParam;
import com.godotvillage.meowkanban.domain.vo.BoardInfoVO;
import com.godotvillage.meowkanban.mapper.BoardMapper;
import com.godotvillage.meowkanban.service.IBoardService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardServiceImpl extends ServiceImpl<BoardMapper, Board> implements IBoardService {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 50;

    @Override
    public PageResult<BoardInfoVO> listBoardInfo(BoardInfoQueryParam param) {
        if (param == null) {
            param = new BoardInfoQueryParam();
        }
        normalizePageParam(param);

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

    private void normalizePageParam(BoardInfoQueryParam param) {
        int pageIndex = param.getPageIndex() == null || param.getPageIndex() < 1
                ? DEFAULT_PAGE
                : param.getPageIndex();
        int pageSize = param.getPageSize() == null || param.getPageSize() < 1
                ? DEFAULT_PAGE_SIZE
                : Math.min(param.getPageSize(), MAX_PAGE_SIZE);

        param.setPageIndex(pageIndex);
        param.setPageSize(pageSize);
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
}
