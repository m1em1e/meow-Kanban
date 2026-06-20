package com.godotvillage.meowkanban.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.godotvillage.meowkanban.common.result.PageResult;
import com.godotvillage.meowkanban.domain.entity.TaskActivity;
import com.godotvillage.meowkanban.domain.param.RecentActivityParam;
import com.godotvillage.meowkanban.domain.vo.RecentActivityVO;
import com.godotvillage.meowkanban.mapper.TaskActivityMapper;
import com.godotvillage.meowkanban.service.ITaskActivityService;
import org.springframework.stereotype.Service;

@Service
public class TaskActivityServiceImpl extends ServiceImpl<TaskActivityMapper, TaskActivity> implements ITaskActivityService {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 50;

    @Override
    public PageResult<RecentActivityVO> getRecentActivity(RecentActivityParam param) {
        if (param == null) {
            param = new RecentActivityParam();
        }
        normalizePageParam(param);

        Page<RecentActivityVO> page = new Page<>(param.getPageIndex(), param.getPageSize());
        IPage<RecentActivityVO> activityPage = baseMapper.getRecentActivity(page, param);
        return PageResult.of(
                activityPage.getRecords(),
                activityPage.getTotal(),
                activityPage.getCurrent(),
                activityPage.getSize(),
                activityPage.getPages()
        );
    }

    private void normalizePageParam(RecentActivityParam param) {
        int pageIndex = param.getPageIndex() == null || param.getPageIndex() < 1
                ? DEFAULT_PAGE
                : param.getPageIndex();
        int pageSize = param.getPageSize() == null || param.getPageSize() < 1
                ? DEFAULT_PAGE_SIZE
                : Math.min(param.getPageSize(), MAX_PAGE_SIZE);

        param.setPageIndex(pageIndex);
        param.setPageSize(pageSize);
    }

}
