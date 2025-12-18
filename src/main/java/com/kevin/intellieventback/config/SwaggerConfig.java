package com.kevin.intellieventback.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${swagger.info.title:${spring.application.name}}")
    private String title;

    @Value("${swagger.info.version:v1.0.0}")
    private String version;

    @Value("${swagger.info.description:API Documentation}")
    private String description;

    @Value("${swagger.info.contact.name:}")
    private String contactName;

    @Value("${swagger.info.contact.email:}")
    private String contactEmail;

    @Value("${swagger.info.contact.url:}")
    private String contactUrl;

    @Value("${swagger.info.license.name:}")
    private String licenseName;

    @Value("${swagger.info.license.url:}")
    private String licenseUrl;

    @Value("${swagger.servers.local.name:本地环境}")
    private String localServerName;

    @Value("${swagger.servers.production.url:}")
    private String productionServerUrl;

    @Value("${swagger.servers.production.name:生产环境}")
    private String productionServerName;

    @Value("${swagger.enable-production-server:false}")
    private boolean enableProductionServer;

    @Bean
    public OpenAPI openAPI() {
        List<Server> servers = new ArrayList<>();

        // 添加本地服务器
        servers.add(new Server()
                .url("http://localhost:" + serverPort)
                .description(localServerName));

        // 如果启用了生产环境服务器且配置了URL，则添加
        if (enableProductionServer && productionServerUrl != null && !productionServerUrl.trim().isEmpty()) {
            servers.add(new Server()
                    .url(productionServerUrl.trim())
                    .description(productionServerName));
        }

        // 创建联系人信息（只有配置了信息才创建）
        Contact contact = null;
        if (contactName != null && !contactName.trim().isEmpty()) {
            contact = new Contact()
                    .name(contactName.trim())
                    .email(contactEmail != null ? contactEmail.trim() : "")
                    .url(contactUrl != null ? contactUrl.trim() : "");
        }

        // 创建许可证信息（只有配置了信息才创建）
        License license = null;
        if (licenseName != null && !licenseName.trim().isEmpty()) {
            license = new License()
                    .name(licenseName.trim())
                    .url(licenseUrl != null ? licenseUrl.trim() : "");
        }

        Info info = new Info()
                .title(title)
                .version(version)
                .description(description);

        // 设置可选的联系人和许可证信息
        if (contact != null) {
            info.contact(contact);
        }
        if (license != null) {
            info.license(license);
        }

        return new OpenAPI()
                .info(info)
                .servers(servers);
    }
}