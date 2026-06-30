package com.godotvillage.meowkanban.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.hutool.core.util.IdUtil;
import com.godotvillage.meowkanban.common.exception.BaseException;
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
        if (param == null || param.getId() == null) {
            throw new BaseException("看板 ID 不能为空");
        }
        if (loginId == null) {
            throw new BaseException("请先登录");
        }

        baseMapper.upsertFavorite(IdUtil.getSnowflakeNextId(), param.getId(), loginId, LocalDateTime.now());
    }

    @Override
    public void delFavorite(IdParam param, Long loginId) {
        if (param == null || param.getId() == null) {
            throw new BaseException("看板 ID 不能为空");
        }
        if (loginId == null) {
            throw new BaseException("请先登录");
        }

        baseMapper.cancelFavorite(param.getId(), loginId, LocalDateTime.now());
    }
}
