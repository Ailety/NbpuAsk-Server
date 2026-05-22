# NbpuAsk-Server

> 宁青千问后端项目  
> College Intelligent Question-Answering System Based on LLM.

**NbpuAsk-Server** 是“宁青千问——基于大模型的校园智能问答系统”的后端项目，基于 Spring Boot 构建，负责用户认证、对话管理、对话分享、历史记录持久化以及大模型流式问答等核心功能。

本项目为毕业设计项目 **宁青千问** 的后端部分，前端项目请见：[NbpuAsk-Web](https://github.com/Ailety/NbpuAsk-Web)

## 项目简介

宁青千问是一个面向校园场景的智能问答系统。后端通过调用大模型应用接口，将用户问题发送给模型，并通过 SSE 方式将模型回答流式返回给前端。

系统主要包含以下能力：

- 用户注册与登录
- JWT 登录认证
- 用户信息读取与更新
- 对话创建、获取、保存、删除
- 历史对话数据持久化
- 大模型流式问答
- 模型回答完成后自动生成对话标题
- 对话公开分享与取消分享
- 分享对话访问统计

## 技术栈

| 类型 | 技术 |
| --- | --- |
| 核心框架 | Spring Boot 2.7.18 |
| Java 版本 | Java 11 |
| Web 框架 | Spring Web |
| 流式响应 | Spring WebFlux / SSE |
| ORM / 持久层 | MyBatis |
| 数据库 | MySQL |
| 数据库连接池 | HikariCP |
| 大模型接口 | 阿里百炼 DashScope SDK |
| 认证方案 | JWT |
| 密码加密 | BCrypt |
| API 文档 | Knife4j |
| 构建工具 | Maven |
| 辅助工具 | Lombok |

## 项目结构

```text
NbpuAsk-Server
├── src/
│   ├── main/
│   │   ├── java/me/Ailety/NbpuAsk/
│   │   │   ├── controller/       # 控制器层
│   │   │   ├── dao/              # 数据访问层
│   │   │   ├── handler/          # 类型处理器
│   │   │   ├── model/            # 实体类与 DTO
│   │   │   ├── service/          # 服务接口
│   │   │   ├── service/Impl/     # 服务实现
│   │   │   ├── util/             # 工具类
│   │   │   └── NbpuAskApplication.java
│   │   └── resources/
│   │       ├── mapper/           # MyBatis Mapper XML
│   │       ├── sql/              # SQL 脚本
│   │       └── application.yml   # 项目配置
├── pom.xml
└── README.md
```

## 核心功能

### 用户模块

- 用户注册
- 用户登录
- JWT Token 生成
- Token 校验
- 用户数据获取与更新

相关接口路径：

```text
POST /user/register
POST /user/login
POST /user/get-data
POST /user/set-data
```

### 认证模块

系统使用 JWT 进行登录状态认证。  
需要认证的接口通过请求头传递 Token：

```http
Authorization: Bearer <token>
```

### 对话模块

支持用户创建、读取、保存、删除对话。

相关接口路径：

```text
POST /conversation/create
POST /conversation/get
POST /conversation/get-all
POST /conversation/set
POST /conversation/delete
POST /conversation/delete-all
```

### 大模型流式问答

系统通过大模型接口获取回答，并使用 SSE 向前端流式返回内容。

相关接口路径：

```text
POST /conversation/runs
```

请求示例：

```json
{
  "query": "学校图书馆开放时间是什么？",
  "conversation_id": "conversation-id"
}
```

响应类型：

```http
Content-Type: text/event-stream
```

前端可以逐步接收模型返回的文本片段，实现类似 ChatGPT 的流式输出效果。

### 自动生成对话标题

当用户在一个新对话中完成首次提问后，后端会根据用户问题和模型回答异步生成一个简短的中文标题，用于历史对话列表展示。

标题生成要求：

- 不超过 10 个汉字或字符
- 不使用标点符号
- 只返回标题本身

### 对话分享模块

支持将指定对话生成公开分享链接，也支持取消分享和读取分享对话。

相关接口路径：

```text
POST /conversation/share
POST /conversation/share/cancel
POST /conversation/shares
GET  /conversation/shared/{conversationId}
```

## 环境要求

- JDK 11+
- Maven 3.6+
- MySQL 8.x
- 可用的 DashScope / 阿里百炼应用接口配置

## 本地运行

### 1. 克隆项目

```bash
git clone https://github.com/Ailety/NbpuAsk-Server.git
cd NbpuAsk-Server
```

### 2. 创建数据库

请先创建数据库，例如：

```sql
CREATE DATABASE nbpuask DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

当前仓库中已提供 `conversation_shares` 分享表 SQL。  
其他业务表如果未随仓库提供，请根据项目实体类、Mapper 文件或已有数据库结构自行补充。

### 3. 配置环境变量

项目配置文件 `application.yml` 会从环境变量或 `.env` 文件中读取敏感配置。

可以在项目根目录创建 `.env` 文件：

```env
DS_URL=jdbc:mysql://localhost:3306/nbpuask?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
DS_USERNAME=root
DS_PASSWORD=your_mysql_password

MODEL_APP_ID=your_dashscope_app_id
DASHSCOPE_API_KEY=your_dashscope_api_key

JWT_SECRET_KEY=your_jwt_secret_key
SECURITY_AES_SECRET_KEY=your_aes_secret_key
```

字段说明：

| 配置项 | 说明 |
| --- | --- |
| `DS_URL` | MySQL 数据库连接地址 |
| `DS_USERNAME` | MySQL 用户名 |
| `DS_PASSWORD` | MySQL 密码 |
| `MODEL_APP_ID` | DashScope / 百炼应用 ID |
| `DASHSCOPE_API_KEY` | DashScope API Key |
| `JWT_SECRET_KEY` | JWT 签名密钥 |
| `SECURITY_AES_SECRET_KEY` | AES 加密密钥 |

### 4. 修改配置

默认服务端口为：

```yaml
server:
  port: 8088
```

如需修改端口，可在 `src/main/resources/application.yml` 中调整。

### 5. 启动项目

使用 Maven 启动：

```bash
mvn spring-boot:run
```

或者先打包再运行：

```bash
mvn clean package
java -jar target/NbpuAsk-1.0-SNAPSHOT.jar
```

启动成功后，后端默认运行在：

```text
http://localhost:8088
```

## 前端联调

前端项目地址：

```text
https://github.com/Ailety/NbpuAsk-Web
```

本地开发时，请确保前端请求地址指向后端服务，例如：

```text
http://localhost:8088
```

生产部署时，推荐通过 Nginx 将前端 `/api` 请求代理到后端服务。

示例：

```nginx
location /api/ {
    proxy_pass http://127.0.0.1:8088/;
    proxy_http_version 1.1;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
}
```

如果需要支持 SSE 流式输出，请注意关闭代理缓冲：

```nginx
proxy_buffering off;
proxy_cache off;
```

## 注意事项

1. 本项目依赖大模型应用接口，必须正确配置 `MODEL_APP_ID` 和 `DASHSCOPE_API_KEY`。
2. 大模型回答质量依赖模型能力和知识库内容，校园类问题需要配合校园知识库才能获得更稳定的回答。
3. JWT、数据库密码、API Key 等敏感信息不要提交到 GitHub。
4. 当前项目为毕业设计项目，部分数据库脚本、部署脚本和异常处理可能仍需继续完善。
5. 如果前端出现流式回答中断，请检查后端日志、模型接口额度、Nginx SSE 配置和浏览器网络请求。

## 相关仓库

- 前端仓库：[NbpuAsk-Web](https://github.com/Ailety/NbpuAsk-Web)
- 后端仓库：[NbpuAsk-Server](https://github.com/Ailety/NbpuAsk-Server)

## License

本项目仅用于毕业设计、学习交流与技术研究。  
如需复用或二次开发，请保留原作者信息。