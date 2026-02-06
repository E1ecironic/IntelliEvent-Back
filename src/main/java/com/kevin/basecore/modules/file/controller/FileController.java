package com.kevin.basecore.modules.file.controller;

import com.kevin.basecore.common.domin.Result;
import com.kevin.basecore.modules.file.entity.SysFile;
import com.kevin.basecore.modules.file.service.SysFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 文件管理 前端控制器
 * </p>
 *
 * @author kevin
 * @since 2026-02-06
 */
@RestController
@RequestMapping("/sys-file")
@Tag(name = "文件管理")
@RequiredArgsConstructor
public class FileController {

    private final SysFileService sysFileService;

    @PostMapping("/upload")
    @Operation(summary = "文件上传")
    public Result<SysFile> upload(
            @Parameter(description = "文件对象", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "业务目录分类(可选)，如: avatar, activity, poster 等。如果不传则直接存入日期目录。", required = false) 
            @RequestParam(value = "dir", required = false) String dir) {
        return Result.success(sysFileService.upload(file, dir));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除文件")
    public Result<Boolean> delete(@PathVariable String id) {
        return Result.success(sysFileService.deleteFile(id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取文件详情")
    public Result<SysFile> getDetail(@PathVariable String id) {
        return Result.success(sysFileService.getById(id));
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "下载文件")
    public void download(@PathVariable String id, HttpServletResponse response) {
        sysFileService.download(id, response);
    }
}
