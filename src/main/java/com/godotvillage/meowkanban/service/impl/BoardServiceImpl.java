package com.godotvillage.meowkanban.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.godotvillage.meowkanban.common.exception.BaseException;
import com.godotvillage.meowkanban.common.result.PageResult;
import com.godotvillage.meowkanban.common.util.LoginUtil;
import com.godotvillage.meowkanban.domain.entity.Board;
import com.godotvillage.meowkanban.domain.entity.BoardMember;
import com.godotvillage.meowkanban.domain.enums.BoardRoleEnum;
import com.godotvillage.meowkanban.domain.param.BoardInfoQueryParam;
import com.godotvillage.meowkanban.domain.param.IdParam;
import com.godotvillage.meowkanban.domain.param.NewBoardParam;
import com.godotvillage.meowkanban.domain.vo.BoardDetailVO;
import com.godotvillage.meowkanban.domain.vo.BoardInfoVO;
import com.godotvillage.meowkanban.mapper.BoardMapper;
import com.godotvillage.meowkanban.mapper.BoardMemberMapper;
import com.godotvillage.meowkanban.service.IBoardService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BoardServiceImpl extends ServiceImpl<BoardMapper, Board> implements IBoardService {

	@Resource
	private BoardMemberMapper boardMemberMapper;

	@Override
	public PageResult<BoardInfoVO> listBoardInfo(BoardInfoQueryParam param) {
		if (param == null) {
			param = new BoardInfoQueryParam();
		}
		param.setUserId(LoginUtil.getLoginId());

		Page<BoardInfoVO> page = new Page<>(param.getPageIndex(), param.getPageSize());
		IPage<BoardInfoVO> boardPage = baseMapper.getBoardInfoList(page, param);
		return PageResult.of(
				boardPage.getRecords(),
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

		Long loginUserId = LoginUtil.getLoginId();
		BoardMember boardMember = null;
		if (loginUserId != null) {
			boardMember = boardMemberMapper.selectOne(Wrappers.<BoardMember>lambdaQuery()
					.eq(BoardMember::getBoardId, param.getId())
					.eq(BoardMember::getUserId, loginUserId)
			);
			if (boardMember == null) {
				boardMember = new BoardMember();
				boardMember.setBoardId(param.getId());
				boardMember.setUserId(loginUserId);
				boardMember.setJoinedTime(LocalDateTime.now());
				boardMember.setRole(BoardRoleEnum.VIEWER.getCode());
				boardMemberMapper.insert(boardMember);
			}
		}

		BoardDetailVO boardDetailVO = baseMapper.getBoardDetail(param.getId());
		if (boardDetailVO == null) {
			throw new BaseException("看板不存在");
		}

		if (boardMember != null) {
			boardDetailVO.setBoardRoleCode(boardMember.getRole());
		}
		return boardDetailVO;
	}
}
