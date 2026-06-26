package com.godotvillage.meowkanban.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.godotvillage.meowkanban.common.result.PageResult;
import com.godotvillage.meowkanban.domain.entity.TaskActivity;
import com.godotvillage.meowkanban.domain.param.IdParam;
import com.godotvillage.meowkanban.domain.param.RecentActivityParam;
import com.godotvillage.meowkanban.domain.param.TaskActivityAddParam;
import com.godotvillage.meowkanban.domain.vo.RecentActivityVO;

public interface ITaskActivityService extends IService<TaskActivity> {

	void addTaskActivity(TaskActivityAddParam param);

    PageResult<RecentActivityVO> getRecentActivity(RecentActivityParam param);

}
