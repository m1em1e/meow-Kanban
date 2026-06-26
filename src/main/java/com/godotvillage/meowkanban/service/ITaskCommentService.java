package com.godotvillage.meowkanban.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.godotvillage.meowkanban.domain.entity.TaskComment;
import com.godotvillage.meowkanban.domain.param.IdParam;
import com.godotvillage.meowkanban.domain.param.TaskCommentAddParam;
import com.godotvillage.meowkanban.domain.vo.CommentVO;

import java.util.List;

/**
 *
 * @author mkdir
 * @since 2026/06/26 16:30
 */
public interface ITaskCommentService extends IService<TaskComment> {
	void addTaskComment(TaskCommentAddParam param, Long loginId);

	void delTaskComment(IdParam param);

	List<CommentVO> getTaskListByTaskId(IdParam param);
}
