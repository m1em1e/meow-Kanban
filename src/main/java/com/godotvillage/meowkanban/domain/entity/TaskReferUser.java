package com.godotvillage.meowkanban.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mk_task_refer_user")
public class TaskReferUser {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private Long taskId;

    private Long userId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}
