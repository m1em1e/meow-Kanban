package com.godotvillage.meowkanban.controller.restController;

import com.godotvillage.meowkanban.common.result.Result;
import com.godotvillage.meowkanban.common.util.LoginUtil;
import com.godotvillage.meowkanban.domain.param.IdParam;
import com.godotvillage.meowkanban.domain.param.TaskCommentAddParam;
import com.godotvillage.meowkanban.domain.vo.CommentVO;
import com.godotvillage.meowkanban.service.ITaskCommentService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 * @author mkdir
 * @since 2026/06/26 16:31
 */
@RestController
@RequestMapping("/api/v1/comment")
public class CommentRestController {

	@Resource
	private ITaskCommentService taskCommentService;

	@PostMapping("/add-task-comment")
	public Result addTaskComment(@RequestBody TaskCommentAddParam param) {
		taskCommentService.addTaskComment(param, LoginUtil.getLoginId());
		return Result.success();
	}

	@DeleteMapping("/del-task-comment")
	public Result delTaskComment(@RequestBody IdParam param) {
		taskCommentService.delTaskComment(param);
		return Result.success();
	}

	@GetMapping("/get-task-comment")
	public Result<List<CommentVO>> getTaskCommentByTaskId(IdParam param) {
		return Result.success(taskCommentService.getTaskListByTaskId(param));
	}

}
