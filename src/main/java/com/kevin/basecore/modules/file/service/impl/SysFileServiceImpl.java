package com.kevin.basecore.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kevin.basecore.modules.file.entity.SysFile;
import com.kevin.basecore.modules.file.mapper.SysFileMapper;
import com.kevin.basecore.modules.file.service.FileStorageService;
import com.kevin.basecore.modules.file.service.SysFileService;
import com.kevin.basecore.modules.system.service.SysConfigService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * <p>
 * 系统文件表 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2026-02-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile> implements SysFileService {

    private final Map<String, FileStorageService> storageServices;
    private final SysConfigService sysConfigService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFile upload(MultipartFile file) {
        return upload(file, "");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFile upload(MultipartFile file, String dir) {
        // 1. 获取当前启用的存储类型
        String type = sysConfigService.getValue("file.storage.type", "LOCAL");
        
        // 2. 获取对应的存储策略
        // 约定 Bean 名称规则: type.toLowerCase() + "FileStorage"
        // 例如: LOCAL -> localFileStorage, ALIYUN -> aliyunFileStorage
        String serviceName = type.toLowerCase() + "FileStorage";
        FileStorageService storageService = storageServices.get(serviceName);
        
        if (storageService == null) {
            throw new RuntimeException("未找到对应的存储策略: " + type);
        }

        // 3. 执行上传
        String url = storageService.upload(file, dir);

        // 4. 保存数据库记录
        SysFile sysFile = new SysFile();
        sysFile.setOriginalName(file.getOriginalFilename());
        
        // 从 URL 中解析出文件名 (假设 URL 结尾是文件名)
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        sysFile.setFileName(fileName);
        
        sysFile.setFileType(getFileExtension(file.getOriginalFilename()));
        sysFile.setFileSize(file.getSize());
        sysFile.setUrl(url);
        sysFile.setStoragePath(url); // 对于本地存储，路径和 URL 可能有对应关系，这里暂存 URL 或相对路径
        sysFile.setStorageType(type);
        sysFile.setCreatedAt(LocalDateTime.now());
        sysFile.setUpdatedAt(LocalDateTime.now());
        
        save(sysFile);
        
        return sysFile;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFile(String id) {
        SysFile sysFile = getById(id);
        if (sysFile == null) {
            return true;
        }

        // 1. 获取对应的存储策略
        String serviceName = sysFile.getStorageType().toLowerCase() + "FileStorage";
        FileStorageService storageService = storageServices.get(serviceName);
        
        if (storageService != null) {
            // 2. 删除物理文件
            try {
                storageService.delete(sysFile.getStoragePath());
            } catch (Exception e) {
                log.error("物理文件删除失败: {}", sysFile.getStoragePath(), e);
                // 物理删除失败不阻断逻辑删除
            }
        }

        // 3. 删除数据库记录
        return removeById(id);
    }

    @Override
    public void download(String id, HttpServletResponse response) {
        SysFile sysFile = getById(id);
        if (sysFile == null) {
            throw new RuntimeException("文件不存在");
        }

        // 1. 获取对应的存储策略
        String serviceName = sysFile.getStorageType().toLowerCase() + "FileStorage";
        FileStorageService storageService = storageServices.get(serviceName);
        
        if (storageService == null) {
            throw new RuntimeException("未找到对应的存储策略");
        }

        // 2. 获取文件流
        try (InputStream inputStream = storageService.getFileStream(sysFile.getStoragePath())) {
            if (inputStream == null) {
                throw new RuntimeException("文件物理资源不存在");
            }
            
            // 3. 设置响应头
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            // 处理中文文件名乱码
            String fileName = URLEncoder.encode(sysFile.getOriginalName(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            
            // 4. 写入响应
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {
            log.error("文件下载失败", e);
            throw new RuntimeException("文件下载失败: " + e.getMessage());
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return "";
    }
}
