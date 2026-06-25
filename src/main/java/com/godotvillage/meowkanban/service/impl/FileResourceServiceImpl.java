package com.godotvillage.meowkanban.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.godotvillage.meowkanban.common.exception.BaseException;
import com.godotvillage.meowkanban.common.util.LoginUtil;
import com.godotvillage.meowkanban.domain.entity.FileResource;
import com.godotvillage.meowkanban.domain.vo.FileResourceContentVO;
import com.godotvillage.meowkanban.domain.vo.FileResourceInfoVO;
import com.godotvillage.meowkanban.mapper.FileResourceMapper;
import com.godotvillage.meowkanban.service.IFileResourceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class FileResourceServiceImpl extends ServiceImpl<FileResourceMapper, FileResource> implements IFileResourceService {

    private static final DateTimeFormatter DATE_PATH_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    @Value("${meow-kanban.resource.storage-dir:data/resources}")
    private String storageDir;

    @Override
    @Transactional
    public FileResourceInfoVO upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BaseException("上传文件不能为空");
        }

        String originalFileName = StringUtils.cleanPath(
                StringUtils.hasText(file.getOriginalFilename()) ? file.getOriginalFilename() : "resource"
        );
        if (originalFileName.contains("..")) {
            throw new BaseException("文件名不合法");
        }

        String relativePath = buildRelativePath(originalFileName);
        Path targetPath = resolveStorageRoot().resolve(relativePath).normalize();

        try {
            Files.createDirectories(targetPath.getParent());
            file.transferTo(targetPath);
        } catch (IOException e) {
            throw new BaseException("资源文件保存失败");
        }

        FileResource fileResource = new FileResource();
        fileResource.setFileName(originalFileName);
        fileResource.setStoragePath(relativePath);
        fileResource.setContentType(file.getContentType());
        fileResource.setFileSize(file.getSize());
        fileResource.setUploadedBy(LoginUtil.getLoginId());
        save(fileResource);

        return toInfo(fileResource);
    }

    @Override
    public FileResourceContentVO loadContent(Long id) {
        if (id == null) {
            throw new BaseException("资源 ID 不能为空");
        }

        FileResource fileResource = getById(id);
        if (fileResource == null || Integer.valueOf(1).equals(fileResource.getDeleted())) {
            throw new BaseException("资源文件不存在");
        }

        Path resourcePath = resolveStorageRoot().resolve(fileResource.getStoragePath()).normalize();
        Path storageRoot = resolveStorageRoot();
        if (!resourcePath.startsWith(storageRoot) || !Files.isRegularFile(resourcePath)) {
            throw new BaseException("资源文件不存在");
        }

        FileResourceContentVO content = new FileResourceContentVO();
        content.setFileName(fileResource.getFileName());
        content.setContentType(fileResource.getContentType());
        content.setFileSize(fileResource.getFileSize());
        content.setResource(new FileSystemResource(resourcePath));
        return content;
    }

    private String buildRelativePath(String originalFileName) {
        String datePath = LocalDate.now().format(DATE_PATH_FORMATTER);
        String extension = "";
        int extensionIndex = originalFileName.lastIndexOf('.');
        if (extensionIndex >= 0 && extensionIndex < originalFileName.length() - 1) {
            extension = originalFileName.substring(extensionIndex);
        }
        return datePath + "/" + UUID.randomUUID() + extension;
    }

    private Path resolveStorageRoot() {
        return Path.of(storageDir).toAbsolutePath().normalize();
    }

    private FileResourceInfoVO toInfo(FileResource fileResource) {
        FileResourceInfoVO info = new FileResourceInfoVO();
        info.setId(fileResource.getId());
        info.setFileName(fileResource.getFileName());
        info.setContentType(fileResource.getContentType());
        info.setFileSize(fileResource.getFileSize());
        info.setUrl("/api/v1/resource/" + fileResource.getId());
        return info;
    }

}
