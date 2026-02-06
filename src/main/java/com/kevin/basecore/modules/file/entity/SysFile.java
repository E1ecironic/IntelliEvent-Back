package com.kevin.basecore.modules.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kevin.basecore.common.domin.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 系统文件表
 * </p>
 *
 * @author kevin
 * @since 2026-02-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_file")
@Schema(description = "系统文件对象")
public class SysFile extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "原文件名")
    private String originalName;

    @Schema(description = "存储文件名")
    private String fileName;

    @Schema(description = "文件类型/扩展名")
    private String fileType;

    @Schema(description = "文件大小(字节)")
    private Long fileSize;

    @Schema(description = "访问URL")
    private String url;

    @Schema(description = "存储路径/对象Key")
    private String storagePath;

    @Schema(description = "存储类型: LOCAL, ALIYUN, MINIO")
    private String storageType;
}
