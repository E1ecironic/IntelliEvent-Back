package com.kevin.basecore.modules.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kevin.basecore.modules.file.entity.SysFile;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 系统文件表 服务类
 * </p>
 *
 * @author kevin
 * @since 2026-02-06
 */
public interface SysFileService extends IService<SysFile> {

    /**
     * 上传文件
     * @param file 文件
     * @return 文件信息
     */
    SysFile upload(MultipartFile file);

    /**
     * 上传文件 (指定目录)
     * @param file 文件
     * @param dir 目录
     * @return 文件信息
     */
    SysFile upload(MultipartFile file, String dir);

    /**
     * 删除文件
     * @param id 文件ID
     * @return 是否成功
     */
    boolean deleteFile(String id);

    /**
     * 下载文件
     * @param id 文件ID
     * @param response 响应对象
     */
    void download(String id, HttpServletResponse response);
}
