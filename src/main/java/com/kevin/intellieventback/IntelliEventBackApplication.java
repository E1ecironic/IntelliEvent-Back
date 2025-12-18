package com.kevin.intellieventback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class IntelliEventBackApplication {

    private static final Logger log = LoggerFactory.getLogger(IntelliEventBackApplication.class);

    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplication(IntelliEventBackApplication.class);
        Environment env = app.run(args).getEnvironment();

        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }

        String serverPort = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        if (contextPath.isBlank()) {
            contextPath = "/";
        }

        log.info("\n" +
                        "=================================================================\n" +
                        "应用 '{}' 启动成功！\n" +
                        "=================================================================\n" +
                        "访问地址:\n" +
                        "本地访问:    {}://localhost:{}{}\n" +
                        "外部访问:    {}://{}:{}{}\n" +
                        "\n" +
                        "API 文档:\n" +
                        "Swagger UI:  {}://localhost:{}{}swagger-ui.html\n" +
                        "OpenAPI:     {}://localhost:{}{}api-docs\n" +
                        "\n" +
                        "配置信息:\n" +
                        "运行端口:    {}\n" +
                        "运行环境:    {}\n" +
                        "=================================================================",
                env.getProperty("spring.application.name", "IntelliEvent Backend"),
                protocol,
                serverPort,
                contextPath,
                protocol,
                InetAddress.getLocalHost().getHostAddress(),
                serverPort,
                contextPath,
                protocol,
                serverPort,
                contextPath,
                protocol,
                serverPort,
                contextPath,
                serverPort,
                env.getActiveProfiles());
    }
}