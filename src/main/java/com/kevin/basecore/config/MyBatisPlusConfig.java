package com.kevin.basecore.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class MyBatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));

        return interceptor;
    }

    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                Object id = getFieldValByName("id", metaObject);
                if (id == null) {
                    setFieldValByName("id", java.util.UUID.randomUUID().toString().replace("-", ""), metaObject);
                }
                Object createdAt = getFieldValByName("createdAt", metaObject);
                if (createdAt == null) {
                    setFieldValByName("createdAt", LocalDateTime.now(), metaObject);
                }
                Object updatedAt = getFieldValByName("updatedAt", metaObject);
                if (updatedAt == null) {
                    setFieldValByName("updatedAt", LocalDateTime.now(), metaObject);
                }
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                Object updatedAt = getFieldValByName("updatedAt", metaObject);
                if (updatedAt != null) {
                    setFieldValByName("updatedAt", LocalDateTime.now(), metaObject);
                }
            }
        };
    }
}