package com.godotvillage.meowkanban.domain.vo;

import lombok.Data;

@Data
public class FileResourceInfoVO {

    private Long id;

    private String fileName;

    private String contentType;

    private Long fileSize;

    private String url;
}
