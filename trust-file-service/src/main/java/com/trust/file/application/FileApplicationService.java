package com.trust.file.application;

import com.trust.file.api.request.FileDeleteReq;
import com.trust.file.api.request.FileDownloadReq;
import com.trust.file.api.request.FileUploadReq;
import com.trust.file.api.response.FileInfoRes;
import com.trust.file.api.response.FileUploadRes;
import com.trust.file.domain.service.StorageStrategy;
import com.trust.file.infrastructure.persistence.model.FileRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class FileApplicationService {

    private final JdbcTemplate jdbcTemplate;
    private final StorageStrategy storageStrategy;

    public FileApplicationService(JdbcTemplate jdbcTemplate, StorageStrategy storageStrategy) {
        this.jdbcTemplate = jdbcTemplate;
        this.storageStrategy = storageStrategy;
    }

    public FileUploadRes upload(FileUploadReq req) {
        byte[] content = Base64.getDecoder().decode(req.getContentBase64());
        String fileId = UUID.randomUUID().toString().replace("-", "");
        String storagePath = storageStrategy.store(fileId, content, req.getFileName());

        jdbcTemplate.update(
                "INSERT INTO sys_file_record (id, file_id, file_name, original_name, file_size, file_type, storage_type, storage_path, url, create_by, create_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                System.currentTimeMillis(), fileId, req.getFileName(), req.getFileName(), (long) content.length,
                req.getFileType(), "LOCAL", storagePath, "/file/download?fileId=" + fileId, null, LocalDateTime.now());

        FileUploadRes res = new FileUploadRes();
        res.setFileId(fileId);
        res.setFileName(req.getFileName());
        res.setUrl("/file/download?fileId=" + fileId);
        res.setFileSize((long) content.length);
        return res;
    }

    public byte[] download(FileDownloadReq req) {
        List<FileRecord> records = jdbcTemplate.query(
                "SELECT * FROM sys_file_record WHERE file_id = ?",
                (rs, rowNum) -> {
                    FileRecord r = new FileRecord();
                    r.setFileId(rs.getString("file_id"));
                    r.setStoragePath(rs.getString("storage_path"));
                    return r;
                }, req.getFileId());
        if (records.isEmpty()) {
            throw new RuntimeException("文件不存在");
        }
        return storageStrategy.retrieve(records.get(0).getStoragePath());
    }

    public FileInfoRes getFileInfo(String fileId) {
        List<FileInfoRes> records = jdbcTemplate.query(
                "SELECT * FROM sys_file_record WHERE file_id = ?",
                (rs, rowNum) -> {
                    FileInfoRes r = new FileInfoRes();
                    r.setFileId(rs.getString("file_id"));
                    r.setFileName(rs.getString("file_name"));
                    r.setOriginalName(rs.getString("original_name"));
                    r.setFileSize(rs.getLong("file_size"));
                    r.setFileType(rs.getString("file_type"));
                    r.setUrl(rs.getString("url"));
                    r.setCreateBy(rs.getString("create_by"));
                    r.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
                    return r;
                }, fileId);
        return records.isEmpty() ? null : records.get(0);
    }

    public void delete(FileDeleteReq req) {
        List<FileRecord> records = jdbcTemplate.query(
                "SELECT * FROM sys_file_record WHERE file_id = ?",
                (rs, rowNum) -> {
                    FileRecord r = new FileRecord();
                    r.setStoragePath(rs.getString("storage_path"));
                    return r;
                }, req.getFileId());
        if (!records.isEmpty()) {
            storageStrategy.delete(records.get(0).getStoragePath());
        }
        jdbcTemplate.update("DELETE FROM sys_file_record WHERE file_id = ?", req.getFileId());
    }
}
