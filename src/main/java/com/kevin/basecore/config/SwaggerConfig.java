package com.kevin.basecore.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${swagger.title:智能活动管理系统 API}")
    private String title;

    @Value("${swagger.description:智能活动管理系统的后端接口文档}")
    private String description;

    @Value("${swagger.version:1.0.0}")
    private String version;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // 指定扫描的包路径
                .apis(RequestHandlerSelectors.basePackage("com.kevin.intellieventback.controller"))
                .paths(PathSelectors.any())
                .build()
                // 解决 LocalDateTime 等类型的序列化问题
                .directModelSubstitute(LocalDate.class, String.class)
                .directModelSubstitute(LocalDateTime.class, Date.class)
                .directModelSubstitute(Date.class, String.class)
                // 启用
                .enable(true);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .version(version)
                .contact(new Contact("Kevin", "", "kevin@example.com"))
                .license("MIT License")
                .licenseUrl("https://opensource.org/licenses/MIT")
                .build();
    }
}