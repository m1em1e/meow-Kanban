package com.godotvillage.meowkanban.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.godotvillage.meowkanban.domain.entity.TaskComment;
import com.godotvillage.meowkanban.domain.entity.User;
import com.godotvillage.meowkanban.domain.param.IdParam;
import com.godotvillage.meowkanban.domain.param.TaskCommentAddParam;
import com.godotvillage.meowkanban.domain.vo.CommentVO;
import com.godotvillage.meowkanban.mapper.TaskCommentMapper;
import com.godotvillage.meowkanban.mapper.UserMapper;
import com.godotvillage.meowkanban.service.ITaskCommentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mkdir
 * @since 2026/06/26 16:30
 */
@Service
public class TaskCommentServiceImpl extends ServiceImpl<TaskCommentMapper, TaskComment> implements ITaskCommentService {

	@Resource
	private UserMapper userMapper;

	@Override
	public void addTaskComment(TaskCommentAddParam param, Long loginId) {
		TaskComment comment = new TaskComment();
		comment.setTaskId(param.getTaskId());
		comment.setContent(param.getContent());
		comment.setUserId(loginId);
		baseMapper.insert(comment);
	}

	@Override
	public void delTaskComment(IdParam param) {
		TaskComment comment = baseMapper.selectById(param.getId());
		comment.setDeleted(1);
		comment.setDeletedTime(LocalDateTime.now());
		baseMapper.updateById(comment);
	}

	@Override
	public List<CommentVO> getTaskListByTaskId(IdParam param) {
		List<TaskComment> comments = baseMapper.selectList(Wrappers.<TaskComment>lambdaQuery()
				.eq(TaskComment::getTaskId, param.getId())
		);
		List<CommentVO> commentVOS = new ArrayList<>();
		for (TaskComment comment : comments) {
			CommentVO commentVO = new CommentVO();
			commentVO.setCommentId(comment.getId());
			commentVO.setContent(comment.getContent());
			commentVO.setUserId(comment.getUserId());
			User user = userMapper.selectById(comment.getUserId());
			commentVO.setNickname(user.getNickname());
			commentVO.setAvatarResourceId(user.getAvatarResourceId());
			commentVO.setCommentTime(comment.getCreatedTime());
			commentVOS.add(commentVO);
		}
		return commentVOS;
	}
}
