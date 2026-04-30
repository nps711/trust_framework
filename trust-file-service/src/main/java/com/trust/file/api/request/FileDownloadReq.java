package com.trust.file.api.request;

import com.trust.common.core.api.BaseRequest;
import jakarta.validation.constraints.NotBlank;

public class FileDownloadReq extends BaseRequest {
    @NotBlank
    private String fileId;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
