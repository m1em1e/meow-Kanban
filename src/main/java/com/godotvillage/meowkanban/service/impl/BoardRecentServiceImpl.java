package com.godotvillage.meowkanban.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.godotvillage.meowkanban.domain.entity.BoardRecent;
import com.godotvillage.meowkanban.mapper.BoardRecentMapper;
import com.godotvillage.meowkanban.service.IBoardRecentService;
import org.springframework.stereotype.Service;

@Service
public class BoardRecentServiceImpl extends ServiceImpl<BoardRecentMapper, BoardRecent> implements IBoardRecentService {
}
