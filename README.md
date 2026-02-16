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
