package com.kevin.basecore.modules.file.service.strategy;

import com.kevin.basecore.modules.file.service.FileStorageService;
import com.kevin.basecore.modules.system.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 本地文件存储策略
 */
@Slf4j
@Service("localFileStorage")
@RequiredArgsConstructor
public class LocalFileStorageStrategy implements FileStorageService {

    private final SysConfigService sysConfigService;

    // 默认存储路径: 用户主目录/intellievent/files
    private static final String DEFAULT_BASE_PATH = System.getProperty("user.home") + File.separator + "intellievent" + File.separator + "files";
    private static final String URL_PREFIX = "/files";

    @Override
    public String upload(MultipartFile file, String dir) {
        try {
            return upload(file.getInputStream(), file.getOriginalFilename(), dir);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public String upload(InputStream inputStream, String fileName, String dir) {
        // 1. 获取基础存储路径
        String basePath = sysConfigService.getValue("file.storage.local.path", DEFAULT_BASE_PATH);
        
        // 2. 构建相对路径 (按日期分文件夹: 2023/10/24)
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String relativePath = (StringUtils.hasText(dir) ? dir + "/" : "") + datePath;
        
        // 3. 构建完整目录并创建
        File directory = new File(basePath, relativePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 4. 生成新文件名 (UUID + 扩展名)
        String suffix = getFileExtension(fileName);
        String newFileName = UUID.randomUUID().toString().replace("-", "") + suffix;
        
        // 5. 保存文件
        File destFile = new File(directory, newFileName);
        try (FileOutputStream outputStream = new FileOutputStream(destFile)) {
            FileCopyUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            log.error("保存文件失败", e);
            throw new RuntimeException("保存文件失败");
        }

        // 6. 返回访问URL (相对路径，前端拼接域名 或 后端统一处理)
        // 格式: /files/2023/10/24/uuid.jpg
        return URL_PREFIX + "/" + relativePath + "/" + newFileName;
    }

    @Override
    public boolean delete(String filePath) {
        if (!StringUtils.hasText(filePath)) {
            return false;
        }
        
        // 解析文件物理路径
        // URL: /files/2023/10/24/uuid.jpg
        // Path: basePath + /2023/10/24/uuid.jpg
        
        if (filePath.startsWith(URL_PREFIX)) {
            String basePath = sysConfigService.getValue("file.storage.local.path", DEFAULT_BASE_PATH);
            String relativePath = filePath.substring(URL_PREFIX.length());
            File file = new File(basePath, relativePath);
            if (file.exists()) {
                return file.delete();
            }
        }
        return false;
    }

    @Override
    public InputStream getFileStream(String filePath) {
        if (!StringUtils.hasText(filePath)) {
            return null;
        }
        
        if (filePath.startsWith(URL_PREFIX)) {
            String basePath = sysConfigService.getValue("file.storage.local.path", DEFAULT_BASE_PATH);
            String relativePath = filePath.substring(URL_PREFIX.length());
            File file = new File(basePath, relativePath);
            if (file.exists()) {
                try {
                    return new java.io.FileInputStream(file);
                } catch (java.io.FileNotFoundException e) {
                    log.error("文件未找到: {}", filePath, e);
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public String getStorageType() {
        return "LOCAL";
    }

    private String getFileExtension(String fileName) {
        if (StringUtils.hasText(fileName) && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return "";
    }
}
