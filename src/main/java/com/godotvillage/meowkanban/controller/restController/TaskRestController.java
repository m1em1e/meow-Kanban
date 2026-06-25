package com.godotvillage.meowkanban.controller.restController;

import com.godotvillage.meowkanban.common.result.Result;
import com.godotvillage.meowkanban.common.util.LoginUtil;
import com.godotvillage.meowkanban.domain.param.IdParam;
import com.godotvillage.meowkanban.domain.param.TaskCardModifyParam;
import com.godotvillage.meowkanban.domain.vo.TaskCardAddParam;
import com.godotvillage.meowkanban.service.ITaskService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author mkdir
 * @since 2026/06/25 15:18
 */
@RestController
@RequestMapping("/api/v1/task")
public class TaskRestController {

	@Resource
	private ITaskService taskService;

	@PostMapping("/add-task-card")
	public Result addTaskCard(@RequestBody TaskCardAddParam param) {
		taskService.addTaskCard(param, LoginUtil.getLoginId());
		return Result.success();
	}

	@DeleteMapping("/del-task-card")
	public Result delTaskCard(@RequestBody IdParam param) {
		taskService.deleteById(new IdParam());
		return Result.success();
	}

	@PutMapping("/modify-task-card")
	public Result modifyTaskCard(@RequestBody TaskCardModifyParam param) {
		taskService.modifyTaskCard(param);
		return Result.success();
	}

}
