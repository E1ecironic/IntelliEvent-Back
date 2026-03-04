# IntelliEvent-Back
基于大模型的智能活动策划与管理平台-后端

author： kevin\
start：  2025.12

## 目标与范围
构建支持“活动全生命周期 + AI 能力”的服务端，提供活动管理、组织权限、AI 生成与知识沉淀能力，并对外提供统一 API。

## 技术栈
- Spring Boot 3 + MyBatis-Plus
- MySQL 8（utf8mb4）
- Swagger / OpenAPI

## 架构分层
- controller：对外 API
- service：业务编排
- mapper：数据访问
- entity / dto：模型与输入输出

## 领域模块规划
- 活动模块：活动、任务、预算、物料、日程
- AI 模块：策划生成、预算分析、风险识别、内容生成
- 知识模块：模板、清单、复盘案例
- 报表模块：指标统计、活动复盘
- 通知模块：邮件/站内通知/消息队列（可选）
- 权限模块：组织、用户、角色、权限

## AI 集成架构
- LLM 适配层：统一封装不同模型供应商
- Prompt 模板中心：模板版本化与可配置化
- 工作流编排：策划生成 -> 预算分析 -> 风险识别 -> 复盘总结
- 结果落库：活动方案、预算建议、风险清单、复盘报告
- 安全与审计：输入脱敏、输出审计、调用日志

## AI 模块能力与接口

### 功能清单
- AI 对话：支持多轮对话、上下文记忆、模型参数覆盖
- 上下文管理：按 contextId/userId 存储与清理
- RAG 检索：基于知识库内容检索并注入上下文
- 知识库管理：保存知识条目，用于 RAG 检索
- 多供应商适配：Ollama 与 OpenAI 兼容接口

### 接口列表

| 功能 | 接口 | 方法 | 说明 |
| --- | --- | --- | --- |
| AI 对话 | /ai/chat | POST | 统一对话入口 |
| 清理上下文 | /ai/context/clear | POST | 按 contextId/userId 清理缓存 |
| 知识检索 | /ai/rag/search | POST | 关键字检索知识库 |
| 保存知识 | /ai/rag/save | POST | 保存/更新知识条目 |

### 请求/响应示例

AI 对话请求示例：
```json
{
  "provider": "ollama",
  "model": "llama3",
  "systemPrompt": "你是活动策划专家",
  "messages": [
    { "role": "user", "content": "生成一份团建活动流程" }
  ],
  "contextId": "ctx-001",
  "userId": "u-1",
  "useContext": true,
  "maxTokens": 800,
  "temperature": 0.7,
  "timeoutMs": 60000,
  "ragEnabled": true,
  "ragQuery": "团建 活动 流程",
  "ragTopK": 3
}
```

AI 对话响应示例：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "requestId": "n3pQb7YxJk8LmP2z",
    "provider": "ollama",
    "model": "llama3",
    "text": "以下是团建活动流程...",
    "contextId": "ctx-001",
    "usage": {
      "promptTokens": 0,
      "completionTokens": 0,
      "totalTokens": 0
    },
    "raw": {}
  }
}
```

RAG 检索请求示例：
```json
{
  "query": "年会 主持稿",
  "limit": 3
}
```

保存知识请求示例：
```json
{
  "title": "年会主持稿",
  "content": "完整主持稿内容",
  "tags": "年会,主持,话术",
  "status": 1
}
```

## 数据模型建议（核心表）
- activity：活动主体
- activity_task：活动任务
- activity_budget：预算项
- activity_material：物料清单
- activity_schedule：日程
- ai_plan：AI 生成的方案
- ai_budget_advice：预算建议
- ai_risk：风险识别
- knowledge_template：模板库
- knowledge_case：复盘案例

## API 设计建议
- /activities/page /activities/{id} /activities/save
- /activities/{id}/tasks /budgets /materials /schedules
- /ai/plan/generate /ai/budget/analyze /ai/risk/identify
- /knowledge/template/page /knowledge/case/page
- /report/activity/summary

## 迭代路线（后端）
1. 完成活动核心表与 CRUD API
2. 完成任务、预算、物料、日程子模块
3. 增加 AI 调用适配与 Prompt 管理
4. 生成结果落库并与活动详情关联
5. 复盘与报表统计接口

## 系统配置

系统配置存储在 `sys_config` 表，通过 `/sys-config` 接口进行维护。

| 配置键 | 说明 | 默认值 | 生效位置 |
| --- | --- | --- | --- |
| user.default.password | 用户注册默认密码 | Aa123456 | 用户注册逻辑 |
| user.default.avatar | 用户默认头像 | 空字符串 | 用户注册逻辑 |
| file.storage.type | 文件存储类型（LOCAL/ALIYUN/MINIO） | LOCAL | 文件上传与下载 |
| file.storage.local.path | 本地存储根路径 | {user.home}/intellievent/files | 本地存储与静态资源映射 |
| security.jwt.expiration | JWT 过期时间（分钟） | 1440 | JWT 生成 |
| security.captcha.enabled | 登录验证码开关 | true | 登录校验 |
| spring.mail.host | 邮件服务器主机 | smtp.qq.com | 邮件发送 |
| spring.mail.port | 邮件服务器端口 | 465 | 邮件发送 |
| spring.mail.username | 邮箱账号 | 无 | 邮件发送 |
| spring.mail.password | 邮箱授权码/密码 | 无 | 邮件发送 |

### AI 模块配置

AI 配置优先级：sys_config → 环境变量 → application.properties。

| 配置键 | 说明 | 默认值 | 生效位置 |
| --- | --- | --- | --- |
| ai.provider.default | 默认供应商 | ollama | AI 对话 |
| ai.provider.ollama.base-url | Ollama 基础地址 | http://localhost:11434 | AI 对话 |
| ai.provider.ollama.model | Ollama 模型 | llama3 | AI 对话 |
| ai.provider.ollama.temperature | Ollama 温度 | 0.7 | AI 对话 |
| ai.provider.ollama.max-tokens | Ollama 最大输出 | 1024 | AI 对话 |
| ai.provider.ollama.timeout-ms | Ollama 超时 | 60000 | AI 对话 |
| ai.provider.openai.base-url | OpenAI 兼容地址 | https://api.openai.com | AI 对话 |
| ai.provider.openai.api-key | OpenAI 兼容密钥 | 空 | AI 对话 |
| ai.provider.openai.model | OpenAI 兼容模型 | gpt-4o-mini | AI 对话 |
| ai.provider.openai.temperature | OpenAI 兼容温度 | 0.7 | AI 对话 |
| ai.provider.openai.max-tokens | OpenAI 兼容最大输出 | 1000 | AI 对话 |
| ai.provider.openai.timeout-ms | OpenAI 兼容超时 | 60000 | AI 对话 |
| ai.provider.openai.chat-path | OpenAI 兼容路径 | /v1/chat/completions | AI 对话 |
| ai.provider.openai.api-key-header | API Key Header | Authorization | AI 对话 |
| ai.provider.openai.api-key-prefix | API Key 前缀 | Bearer  | AI 对话 |
| ai.context.ttl-seconds | 上下文有效期(秒) | 3600 | 上下文缓存 |
| ai.context.max-messages | 上下文最大消息数 | 20 | 上下文缓存 |
| ai.context.key-prefix | 上下文缓存前缀 | ai:ctx: | 上下文缓存 |
| ai.rng.id-length | 随机 ID 长度 | 16 | 请求/上下文 ID |
| ai.rng.charset | 随机字符集 | a-zA-Z0-9 | 请求/上下文 ID |
