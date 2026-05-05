package com.trust.file.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.trust.common.core.error.BusinessException;
import com.trust.file.api.request.FileDeleteReq;
import com.trust.file.api.request.FileDownloadReq;
import com.trust.file.api.request.FileUploadReq;
import com.trust.file.api.response.FileInfoRes;
import com.trust.file.api.response.FileUploadRes;
import com.trust.file.domain.service.StorageStrategy;
import com.trust.file.infrastructure.persistence.mapper.FileRecordMapper;
import com.trust.file.infrastructure.persistence.model.FileRecord;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.UUID;

@Service
public class FileApplicationService {

    private final FileRecordMapper fileRecordMapper;
    private final StorageStrategy storageStrategy;

    public FileApplicationService(FileRecordMapper fileRecordMapper, StorageStrategy storageStrategy) {
        this.fileRecordMapper = fileRecordMapper;
        this.storageStrategy = storageStrategy;
    }

    public FileUploadRes upload(FileUploadReq req) {
        byte[] content = Base64.getDecoder().decode(req.getContentBase64());
        String fileId = UUID.randomUUID().toString().replace("-", "");
        String storagePath = storageStrategy.store(fileId, content, req.getFileName());
        String url = "/file/download?fileId=" + fileId;

        FileRecord record = new FileRecord();
        record.setId(System.currentTimeMillis());
        record.setFileId(fileId);
        record.setFileName(req.getFileName());
        record.setOriginalName(req.getFileName());
        record.setFileSize((long) content.length);
        record.setFileType(req.getFileType());
        record.setStorageType("LOCAL");
        record.setStoragePath(storagePath);
        record.setUrl(url);
        fileRecordMapper.insert(record);

        FileUploadRes res = new FileUploadRes();
        res.setFileId(fileId);
        res.setFileName(req.getFileName());
        res.setUrl(url);
        res.setFileSize((long) content.length);
        return res;
    }

    public byte[] download(FileDownloadReq req) {
        FileRecord record = findByFileId(req.getFileId());
        if (record == null) {
            throw new BusinessException("文件不存在");
        }
        return storageStrategy.retrieve(record.getStoragePath());
    }

    public FileInfoRes getFileInfo(String fileId) {
        FileRecord record = findByFileId(fileId);
        if (record == null) {
            return null;
        }
        FileInfoRes res = new FileInfoRes();
        res.setFileId(record.getFileId());
        res.setFileName(record.getFileName());
        res.setOriginalName(record.getOriginalName());
        res.setFileSize(record.getFileSize());
        res.setFileType(record.getFileType());
        res.setUrl(record.getUrl());
        res.setCreateBy(record.getCreateBy());
        res.setCreateTime(record.getCreateTime());
        return res;
    }

    public void delete(FileDeleteReq req) {
        FileRecord record = findByFileId(req.getFileId());
        if (record != null) {
            storageStrategy.delete(record.getStoragePath());
            fileRecordMapper.deleteById(record.getId());
        }
    }

    private FileRecord findByFileId(String fileId) {
        return fileRecordMapper.selectOne(new LambdaQueryWrapper<FileRecord>()
                .eq(FileRecord::getFileId, fileId));
    }
}
