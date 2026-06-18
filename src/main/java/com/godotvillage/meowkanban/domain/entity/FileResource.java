package com.godotvillage.meowkanban.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mk_file_resource")
public class FileResource {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private String fileName;

    private String storagePath;

    private String contentType;

    private Long fileSize;

    private Long uploadedBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableLogic
    private Integer deleted;

    private LocalDateTime deletedTime;
}
