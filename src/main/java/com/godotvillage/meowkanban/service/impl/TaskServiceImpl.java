package com.godotvillage.meowkanban.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.godotvillage.meowkanban.domain.param.TaskCardModifyParam;
import com.godotvillage.meowkanban.domain.entity.Task;
import com.godotvillage.meowkanban.domain.param.IdParam;
import com.godotvillage.meowkanban.domain.vo.TaskCardAddParam;
import com.godotvillage.meowkanban.mapper.BoardSectionMapper;
import com.godotvillage.meowkanban.mapper.TaskMapper;
import com.godotvillage.meowkanban.service.ITaskService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author mkdir
 * @since 2026/06/25 10:09
 */
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements ITaskService {

	@Resource
	private BoardSectionMapper boardSectionMapper;

	@Override
	@Transactional
	public void addTaskCard(TaskCardAddParam param, Long loginId) {
		Task task = new Task();
		task.setBoardId(param.getBoardId());
		task.setSectionId(param.getSectionId());
		task.setTaskNo(createNextTaskNo(param.getBoardId()));
		task.setTitle(param.getTitle().trim());
		task.setDescription(StringUtils.hasText(param.getDescription()) ? param.getDescription().trim() : null);
		task.setDueDate(param.getDueDate());
		task.setPriority(param.getPriority() == null ? 1 : param.getPriority());
		task.setBlocked(param.getBlocked() == null ? 0 : param.getBlocked());
		task.setSortOrder(param.getSort() == null ? nextSortOrder(param.getBoardId(), param.getSectionId()) : param.getSort());
		task.setCreaterId(loginId);
		task.setUpdaterId(loginId);

		baseMapper.insert(task);
	}

	@Override
	public void deleteById(IdParam param) {
		Task task = baseMapper.selectById(param.getId());
		task.setDeleted(1);
		task.setDeletedTime(LocalDateTime.now());
		baseMapper.updateById(task);
	}

	@Override
	public void modifyTaskCard(TaskCardModifyParam param) {
		Task task = baseMapper.selectById(param.getId());
		if (param.getSectionId() != null) {
			task.setSectionId(param.getSectionId());
		}
		if (param.getTitle() != null) {
			task.setTitle(param.getTitle());
		}
		if (param.getDescription() != null) {
			task.setDescription(param.getDescription());
		}
		if (param.getDueDate() != null) {
			task.setDueDate(param.getDueDate());
		}
		if (param.getPriority() != null) {
			task.setPriority(param.getPriority());
		}
		if (param.getBlocked() != null) {
			task.setBlocked(param.getBlocked());
		}
		if (param.getSort() != null) {
			task.setSortOrder(param.getSort());
		}
		baseMapper.updateById(task);
	}

	private String createNextTaskNo(Long boardId) {
		int nextNo = baseMapper.selectList(Wrappers.<Task>lambdaQuery()
						.select(Task::getTaskNo)
						.eq(Task::getBoardId, boardId)
						.eq(Task::getDeleted, 0)
				).stream()
				.map(Task::getTaskNo)
				.map(this::parseTaskNo)
				.filter(Objects::nonNull)
				.max(Integer::compareTo)
				.orElse(0) + 1;
		return "MK-" + nextNo;
	}

	private Integer parseTaskNo(String taskNo) {
		if (!StringUtils.hasText(taskNo) || !taskNo.startsWith("MK-")) {
			return null;
		}
		try {
			return Integer.parseInt(taskNo.substring(3));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private int nextSortOrder(Long boardId, Long sectionId) {
		List<Task> tasks = baseMapper.selectList(Wrappers.<Task>lambdaQuery()
				.select(Task::getSortOrder)
				.eq(Task::getBoardId, boardId)
				.eq(Task::getSectionId, sectionId)
				.eq(Task::getDeleted, 0)
		);
		return tasks.stream()
				.map(Task::getSortOrder)
				.filter(Objects::nonNull)
				.max(Integer::compareTo)
				.orElse(0) + 10;
	}

}
