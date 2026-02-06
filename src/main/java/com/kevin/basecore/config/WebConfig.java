package com.kevin.basecore.config;

import com.kevin.basecore.modules.system.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final SysConfigService sysConfigService;

    // 默认存储路径: 用户主目录/intellievent/files
    private static final String DEFAULT_BASE_PATH = System.getProperty("user.home") + File.separator + "intellievent" + File.separator + "files";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取配置的本地存储路径
        // 注意：这里是在应用启动时读取配置，如果运行时修改了数据库配置，需要重启服务才能生效
        String localPath = "";
        try {
            localPath = sysConfigService.getValue("file.storage.local.path", DEFAULT_BASE_PATH);
        } catch (Exception e) {
            log.warn("加载文件存储配置失败，使用默认路径: {}", e.getMessage());
            localPath = DEFAULT_BASE_PATH;
        }

        // 确保路径以 / 结尾
        if (!localPath.endsWith(File.separator)) {
            localPath += File.separator;
        }

        log.info("配置静态资源映射: /files/** -> file:{}", localPath);
        
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + localPath);
                
        // 映射 Swagger
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
