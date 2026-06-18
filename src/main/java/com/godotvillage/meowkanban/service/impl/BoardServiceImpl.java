package com.godotvillage.meowkanban.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.godotvillage.meowkanban.common.result.PageResult;
import com.godotvillage.meowkanban.domain.entity.Board;
import com.godotvillage.meowkanban.domain.entity.User;
import com.godotvillage.meowkanban.domain.param.BoardInfoQueryParam;
import com.godotvillage.meowkanban.domain.vo.BoardInfo;
import com.godotvillage.meowkanban.mapper.BoardMapper;
import com.godotvillage.meowkanban.mapper.UserMapper;
import com.godotvillage.meowkanban.service.IBoardService;
import jakarta.annotation.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class BoardServiceImpl extends ServiceImpl<BoardMapper, Board> implements IBoardService {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 50;

    @Resource
    private UserMapper userMapper;

    @Override
    public PageResult<BoardInfo> listBoardInfo(BoardInfoQueryParam param) {
        if (param == null) {
            param = new BoardInfoQueryParam();
        }
        if (StringUtils.hasText(param.getKeyword())) {
            param.setKeyword(param.getKeyword().trim());
        }
        normalizePageParam(param);
        param.setUserId(getCurrentUserId());

        Page<Board> page = new Page<>(param.getPageIndex(), param.getPageSize());
        IPage<Board> boardPage = baseMapper.getBoardInfoList(page, param);
        List<BoardInfo> records = boardPage.getRecords().stream()
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

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String username = authentication.getName();
        if (!StringUtils.hasText(username) || "anonymousUser".equals(username)) {
            return null;
        }

        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getUsername, username)
                .eq(User::getDeleted, 0));
        return user == null ? null : user.getId();
    }

    private BoardInfo toBoardInfo(Board board) {
        BoardInfo boardInfo = new BoardInfo();
        boardInfo.setId(board.getId());
        boardInfo.setName(board.getName());
        boardInfo.setDescription(board.getDescription());
        boardInfo.setCoverResourceId(board.getCoverResourceId());
        boardInfo.setOwnerId(board.getOwnerId());
        boardInfo.setVisibility(board.getVisibility());
        return boardInfo;
    }
}
