package com.godotvillage.meowkanban.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.godotvillage.meowkanban.common.exception.BaseException;
import com.godotvillage.meowkanban.domain.entity.BoardSection;
import com.godotvillage.meowkanban.domain.param.BoardSectionAddParam;
import com.godotvillage.meowkanban.domain.param.BoardSectionModifyParam;
import com.godotvillage.meowkanban.mapper.BoardSectionMapper;
import com.godotvillage.meowkanban.service.IBoardSectionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 *
 * @author mkdir
 * @since 2026/06/25 10:16
 */
@Service
public class BoardSectionServiceImpl extends ServiceImpl<BoardSectionMapper, BoardSection> implements IBoardSectionService {

	@Resource
	private BoardSectionMapper boardSectionMapper;

	@Override
	@Transactional
	public void modifySectionSort(BoardSectionModifyParam param) {
		if (param.getBoardId() == null) {
			throw new BaseException("看板 ID 不能为空");
		}
		if (param.getSourceSort() == null || param.getTargetSort() == null) {
			throw new BaseException("分区排序值不能为空");
		}
		if (Objects.equals(param.getSourceSort(), param.getTargetSort())) {
			return;
		}

		List<BoardSection> boardSections = boardSectionMapper.selectList(Wrappers.<BoardSection>lambdaQuery()
				.eq(BoardSection::getBoardId, param.getBoardId())
				.eq(BoardSection::getDeleted, 0)
				.orderByAsc(BoardSection::getSortOrder)
				.orderByAsc(BoardSection::getId)
		);
		if (boardSections.isEmpty()) {
			throw new BaseException("看板分区不存在");
		}

		boardSections.sort(Comparator.comparing(BoardSection::getSortOrder).thenComparing(BoardSection::getId));
		int sourceIndex = findSectionIndex(boardSections, param.getSourceSort());
		int targetIndex = findSectionIndex(boardSections, param.getTargetSort());
		if (sourceIndex < 0 || targetIndex < 0) {
			throw new BaseException("分区排序值不存在");
		}

		BoardSection movedSection = boardSections.remove(sourceIndex);
		boardSections.add(targetIndex, movedSection);

		for (int i = 0; i < boardSections.size(); i++) {
			BoardSection boardSection = boardSections.get(i);
			int nextSortOrder = (i + 1) * 10;
			if (!Objects.equals(boardSection.getSortOrder(), nextSortOrder)) {
				boardSection.setSortOrder(nextSortOrder);
				boardSectionMapper.updateById(boardSection);
			}
		}
	}

	@Override
	@Transactional
	public void addSectionCard(BoardSectionAddParam param) {
		if (param == null || param.getBoardId() == null) {
			throw new BaseException("看板 ID 不能为空");
		}
		if (!StringUtils.hasText(param.getBoardName())) {
			throw new BaseException("分区名称不能为空");
		}
		if (param.getSort() == null) {
			throw new BaseException("分区排序值不能为空");
		}

		String title = param.getBoardName().trim();
		BoardSection boardSection = new BoardSection();
		boardSection.setBoardId(param.getBoardId());
		boardSection.setCode(createUniqueSectionCode(param.getBoardId(), title));
		boardSection.setTitle(title);
		boardSection.setSortOrder(param.getSort());
		baseMapper.insert(boardSection);
	}

	@Override
	public void deleteById(Long id) {
		BoardSection boardSection = boardSectionMapper.selectById(id);
		boardSection.setDeleted(1);
		boardSection.setDeletedTime(LocalDateTime.now());
		boardSectionMapper.updateById(boardSection);
	}

	private String createUniqueSectionCode(Long boardId, String title) {
		String baseCode = PinyinUtil.getFirstLetter(title, "")
				.toUpperCase(Locale.ROOT)
				.replaceAll("[^A-Z0-9]+", "-")
				.replaceAll("^-+|-+$", "");
		if (!StringUtils.hasText(baseCode)) {
			baseCode = "SECTION";
		}
		if (baseCode.length() > 40) {
			baseCode = baseCode.substring(0, 40).replaceAll("-+$", "");
		}

		String code = baseCode;
		int index = 1;
		while (sectionCodeExists(boardId, code)) {
			code = baseCode + index;
			index++;
		}
		return code;
	}

	private boolean sectionCodeExists(Long boardId, String code) {
		Long count = boardSectionMapper.selectCount(Wrappers.<BoardSection>lambdaQuery()
				.eq(BoardSection::getBoardId, boardId)
				.eq(BoardSection::getCode, code)
				.eq(BoardSection::getDeleted, 0)
		);
		return count != null && count > 0;
	}

	private int findSectionIndex(List<BoardSection> boardSections, Integer sortOrder) {
		for (int i = 0; i < boardSections.size(); i++) {
			if (Objects.equals(boardSections.get(i).getSortOrder(), sortOrder)) {
				return i;
			}
		}
		return -1;
	}
}
