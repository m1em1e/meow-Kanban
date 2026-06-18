package com.godotvillage.meowkanban.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("mk_task")
public class Task {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private Long boardId;

    private Long sectionId;

    private String taskNo;

    private String title;

    private String description;

    private Long ownerId;

    private LocalDate dueDate;

    private String priority;

    private Integer blocked;

    private Integer sortOrder;

    private Long createdBy;

    private Long updatedBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    @TableLogic
    private Integer deleted;

    private LocalDateTime deletedTime;
}
