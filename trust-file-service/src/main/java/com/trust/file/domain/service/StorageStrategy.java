package com.trust.file.domain.service;

public interface StorageStrategy {

    String store(String fileId, byte[] content, String fileName);

    byte[] retrieve(String storagePath);

    void delete(String storagePath);
}
