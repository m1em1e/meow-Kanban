package com.godotvillage.meowkanban.controller.restController;

import com.godotvillage.meowkanban.common.result.PageResult;
import com.godotvillage.meowkanban.common.result.Result;
import com.godotvillage.meowkanban.domain.param.RecentActivityParam;
import com.godotvillage.meowkanban.domain.vo.RecentActivityVO;
import com.godotvillage.meowkanban.service.ITaskActivityService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/activity")
public class TaskActivityRestController {

    @Resource
    private ITaskActivityService taskActivityService;

    @GetMapping("/recent-activity")
    public Result<PageResult<RecentActivityVO>> getRecentActivity(RecentActivityParam param) {
        return Result.success(taskActivityService.getRecentActivity(param));
    }

}
