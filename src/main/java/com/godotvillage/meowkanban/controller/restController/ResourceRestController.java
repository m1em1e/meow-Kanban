package com.godotvillage.meowkanban.controller.restController;

import com.godotvillage.meowkanban.common.result.Result;
import com.godotvillage.meowkanban.domain.vo.FileResourceContentVO;
import com.godotvillage.meowkanban.domain.vo.FileResourceInfoVO;
import com.godotvillage.meowkanban.service.IFileResourceService;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/resource")
public class ResourceRestController {

    private final IFileResourceService fileResourceService;

    public ResourceRestController(IFileResourceService fileResourceService) {
        this.fileResourceService = fileResourceService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<FileResourceInfoVO> upload(@RequestParam("file") MultipartFile file) {
        return Result.success(fileResourceService.upload(file));
    }

    @GetMapping("/{id}")
    public ResponseEntity<org.springframework.core.io.Resource> getResource(@PathVariable Long id) {
        FileResourceContentVO content = fileResourceService.loadContent(id);
        MediaType mediaType = StringUtils.hasText(content.getContentType())
                ? MediaType.parseMediaType(content.getContentType())
                : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(content.getFileSize())
                .cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS).cachePublic())
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline()
                                .filename(content.getFileName(), StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .body(content.getResource());
    }

}
