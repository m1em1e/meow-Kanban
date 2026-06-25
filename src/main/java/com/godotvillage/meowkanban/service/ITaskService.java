package com.godotvillage.meowkanban.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.godotvillage.meowkanban.domain.param.TaskCardModifyParam;
import com.godotvillage.meowkanban.domain.entity.Task;
import com.godotvillage.meowkanban.domain.param.IdParam;
import com.godotvillage.meowkanban.domain.vo.TaskCardAddParam;

/**
 *
 * @author mkdir
 * @since 2026/06/25 10:08
 */
public interface ITaskService extends IService<Task> {
	void addTaskCard(TaskCardAddParam param, Long loginId);

	void deleteById(IdParam param);

	void modifyTaskCard(TaskCardModifyParam param);
}
