# IntelliEvent-Back
基于大模型的智能活动策划与管理平台-后端

# IntelliEvent Backend 启动指南

## 快速启动

### 1. 配置数据库（可选）
- 开发环境：已配置H2内存数据库，无需额外配置
- 生产环境：修改 `application-prod.properties` 中的数据库配置

### 2. 配置OpenAI API（可选）
如果需要AI功能：
- 在 `application.properties` 中设置 `spring.ai.openai.api-key=你的密钥`
- 或设置环境变量 `OPENAI_API_KEY`

### 3. 运行应用
```bash
# 方式1：使用Maven
mvn spring-boot:run

# 方式2：运行jar包
java -jar target/intellievent-back-1.0.0.jar

# 方式3：指定环境
java -jar target/intellievent-back-1.0.0.jar --spring.profiles.active=prod