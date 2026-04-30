package com.trust.file.api.controller;

import com.trust.common.core.api.R;
import com.trust.file.api.request.FileDeleteReq;
import com.trust.file.api.request.FileDownloadReq;
import com.trust.file.api.request.FileUploadReq;
import com.trust.file.api.response.FileInfoRes;
import com.trust.file.api.response.FileUploadRes;
import com.trust.file.application.FileApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/file")
public class FileController {

    private final FileApplicationService fileApplicationService;

    public FileController(FileApplicationService fileApplicationService) {
        this.fileApplicationService = fileApplicationService;
    }

    @PostMapping("/upload")
    public R<FileUploadRes> upload(@Valid @RequestBody FileUploadReq req) {
        return R.success(fileApplicationService.upload(req));
    }

    @PostMapping("/download")
    public ResponseEntity<byte[]> download(@Valid @RequestBody FileDownloadReq req) {
        byte[] content = fileApplicationService.download(req);
        FileInfoRes info = fileApplicationService.getFileInfo(req.getFileId());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", info != null ? info.getFileName() : req.getFileId());
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

    @PostMapping("/info")
    public R<FileInfoRes> info(@Valid @RequestBody FileDownloadReq req) {
        return R.success(fileApplicationService.getFileInfo(req.getFileId()));
    }

    @PostMapping("/delete")
    public R<Boolean> delete(@Valid @RequestBody FileDeleteReq req) {
        fileApplicationService.delete(req);
        return R.success(true);
    }
}
