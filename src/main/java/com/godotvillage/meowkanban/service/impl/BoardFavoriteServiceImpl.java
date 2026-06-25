package com.godotvillage.meowkanban.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.godotvillage.meowkanban.domain.entity.BoardFavorite;
import com.godotvillage.meowkanban.domain.param.IdParam;
import com.godotvillage.meowkanban.mapper.BoardFavoriteMapper;
import com.godotvillage.meowkanban.service.IBoardFavoriteService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BoardFavoriteServiceImpl extends ServiceImpl<BoardFavoriteMapper, BoardFavorite> implements IBoardFavoriteService {

    @Override
    public void addFavorite(IdParam param, Long loginId) {
        BoardFavorite boardFavorite = new BoardFavorite();
        boardFavorite.setBoardId(param.getId());
        boardFavorite.setUserId(loginId);
        baseMapper.insert(boardFavorite);
    }

    @Override
    public void delFavorite(IdParam param) {
        BoardFavorite boardFavorite = baseMapper.selectById(param.getId());
        boardFavorite.setDeleted(1);
        boardFavorite.setDeletedTime(LocalDateTime.now());
        baseMapper.updateById(boardFavorite);
    }
}
