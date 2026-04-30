package com.trust.file.domain.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class LocalStorageStrategy implements StorageStrategy {

    @Value("${trust.file.storage.path:${user.home}/trust-file-storage}")
    private String storagePath;

    @Override
    public String store(String fileId, byte[] content, String fileName) {
        try {
            Path dir = Paths.get(storagePath);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            Path filePath = dir.resolve(fileId + "_" + fileName);
            Files.write(filePath, content);
            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("文件存储失败", e);
        }
    }

    @Override
    public byte[] retrieve(String storagePath) {
        try {
            return Files.readAllBytes(Paths.get(storagePath));
        } catch (IOException e) {
            throw new RuntimeException("文件读取失败", e);
        }
    }

    @Override
    public void delete(String storagePath) {
        try {
            Files.deleteIfExists(Paths.get(storagePath));
        } catch (IOException e) {
            throw new RuntimeException("文件删除失败", e);
        }
    }
}
