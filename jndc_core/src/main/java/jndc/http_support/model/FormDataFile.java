package jndc.http_support.model;

import lombok.Data;

/**
 * 表单文件模型
 */
@Data
public class FormDataFile {
    private String fileName;

    private byte[] fileData;

    public void release() {
        this.fileData = null;
    }
}
