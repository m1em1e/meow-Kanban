package com.godotvillage.meowkanban.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.godotvillage.meowkanban.domain.entity.BoardFavorite;
import com.godotvillage.meowkanban.domain.param.IdParam;

public interface IBoardFavoriteService extends IService<BoardFavorite> {

    void addFavorite(IdParam param, Long loginId);

    void delFavorite(IdParam param);

}
