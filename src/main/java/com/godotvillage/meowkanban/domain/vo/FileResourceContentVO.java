package com.godotvillage.meowkanban.domain.vo;

import lombok.Data;
import org.springframework.core.io.Resource;

@Data
public class FileResourceContentVO {

    private String fileName;

    private String contentType;

    private Long fileSize;

    private Resource resource;
}
