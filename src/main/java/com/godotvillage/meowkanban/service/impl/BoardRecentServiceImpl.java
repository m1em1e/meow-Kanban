package com.godotvillage.meowkanban.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.godotvillage.meowkanban.domain.entity.BoardRecent;
import com.godotvillage.meowkanban.mapper.BoardRecentMapper;
import com.godotvillage.meowkanban.service.IBoardRecentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BoardRecentServiceImpl extends ServiceImpl<BoardRecentMapper, BoardRecent> implements IBoardRecentService {

	@Override
	@Transactional
	public void accessBoard(Long id, Long loginId) {
		BoardRecent boardRecent = baseMapper.selectOne(Wrappers.<BoardRecent>lambdaQuery()
				.eq(BoardRecent::getBoardId, id)
				.eq(BoardRecent::getUserId, loginId)
		);

		if (boardRecent == null) {
			boardRecent = new BoardRecent();
			boardRecent.setBoardId(id);
			boardRecent.setUserId(loginId);
			boardRecent.setLastActiveTime(LocalDateTime.now());
			boardRecent.setActiveCount(1);
		} else {
			boardRecent.setActiveCount(boardRecent.getActiveCount() + 1);
		}

		this.save(boardRecent);
	}

}
