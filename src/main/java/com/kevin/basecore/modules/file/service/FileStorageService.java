package com.kevin.basecore.modules.file.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

/**
 * 文件存储策略接口
 */
public interface FileStorageService {

    /**
     * 文件上传
     * @param file 文件对象
     * @param dir 存储目录(相对路径)
     * @return 存储后的文件访问URL
     */
    String upload(MultipartFile file, String dir);

    /**
     * 文件上传 (流式)
     * @param inputStream 文件流
     * @param fileName 文件名
     * @param dir 存储目录(相对路径)
     * @return 存储后的文件访问URL
     */
    String upload(InputStream inputStream, String fileName, String dir);

    /**
     * 删除文件
     * @param filePath 文件路径或URL
     * @return 是否删除成功
     */
    boolean delete(String filePath);

    /**
     * 获取文件流
     * @param filePath 文件路径或URL
     * @return 文件输入流
     */
    InputStream getFileStream(String filePath);

    /**
     * 获取存储类型
     * @return 存储类型标识
     */
    String getStorageType();
}
