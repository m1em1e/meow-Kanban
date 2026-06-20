package com.godotvillage.meowkanban.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.godotvillage.meowkanban.domain.entity.FileResource;
import com.godotvillage.meowkanban.domain.vo.FileResourceContentVO;
import com.godotvillage.meowkanban.domain.vo.FileResourceInfoVO;
import org.springframework.web.multipart.MultipartFile;

public interface IFileResourceService extends IService<FileResource> {

    FileResourceInfoVO upload(MultipartFile file);

    FileResourceContentVO loadContent(Long id);
}
