package com.godotvillage.meowkanban.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.godotvillage.meowkanban.domain.entity.TaskActivity;
import com.godotvillage.meowkanban.domain.param.RecentActivityParam;
import com.godotvillage.meowkanban.domain.vo.RecentActivityVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TaskActivityMapper extends BaseMapper<TaskActivity> {

    IPage<RecentActivityVO> getRecentActivity(IPage<RecentActivityVO> page, @Param("param") RecentActivityParam param);

}
