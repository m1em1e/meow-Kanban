package com.godotvillage.meowkanban.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.hutool.core.util.IdUtil;
import com.godotvillage.meowkanban.common.exception.BaseException;
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
		if (id == null) {
			throw new BaseException("看板 ID 不能为空");
		}
		if (loginId == null) {
			throw new BaseException("请先登录");
		}

		baseMapper.upsertRecent(IdUtil.getSnowflakeNextId(), id, loginId, LocalDateTime.now());
	}

}
