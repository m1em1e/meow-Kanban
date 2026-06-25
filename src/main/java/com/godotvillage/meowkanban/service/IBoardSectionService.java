package com.godotvillage.meowkanban.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.godotvillage.meowkanban.domain.entity.BoardSection;
import com.godotvillage.meowkanban.domain.param.BoardSectionAddParam;
import com.godotvillage.meowkanban.domain.param.BoardSectionModifyParam;

/**
 *
 * @author mkdir
 * @since 2026/06/25 10:16
 */
public interface IBoardSectionService extends IService<BoardSection> {

	void modifySectionSort(BoardSectionModifyParam param);

	void addSectionCard(BoardSectionAddParam param);

	void deleteById(Long id);
}
