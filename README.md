# 🔍 搜索统计分析系统

类似百方的搜索网站，支持搜索功能并收集搜索记录，用于热点发现。

## 🏗️ 技术架构

| 前端 | 后端 | 数据库 |
|------|------|--------|
| Vue 3 + Vite | Spring Boot 3.x | MySQL 8.0 |
| Tailwind CSS | Java 17 | |
| ECharts | JPA | |

## 📁 项目结构

```
search-engine/
├── backend/                 # Spring Boot 后端
│   ├── pom.xml            # Maven 配置
│   └── src/main/
│       ├── java/com/search/
│       │   ├── SearchEngineApplication.java
│       │   ├── entity/SearchRecord.java
│       │   ├── repository/SearchRecordRepository.java
│       │   ├── service/SearchRecordService.java
│       │   └── controller/SearchController.java
│       └── resources/application.yml
│
├── frontend/               # Vue 3 前端
│   ├── package.json
│   ├── vite.config.js
│   ├── tailwind.config.js
│   ├── index.html
│   └── src/
│       ├── main.js
│       ├── App.vue
│       └── style.css
│
└── README.md
```

## 🚀 快速启动

### 1. 安装 MySQL

```bash
# Docker 方式（推荐）
docker run -d \
  --name mysql-search \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root123 \
  -e MYSQL_DATABASE=search_engine \
  mysql:8

# 或本地安装
yum install mysql-server
systemctl start mysqld
```

### 2. 初始化数据库

```sql
CREATE DATABASE search_engine CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Spring Boot 会自动创建表
```

### 3. 启动后端

```bash
cd backend
./mvnw spring-boot:run

# 或打包运行
./mvnw package
java -jar target/search-engine-1.0.0.jar
```

后端运行在: http://localhost:8080

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端运行在: http://localhost:3000

## 📡 API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/search?keyword=xxx` | 执行搜索（记录并跳转） |
| GET | `/api/search/hot` | 获取热门搜索词 |
| GET | `/api/search/hot/detail` | 获取热门搜索词（带次数） |
| GET | `/api/search/trend?hours=24` | 获取搜索趋势 |
| GET | `/api/search/recent` | 获取最近搜索 |
| GET | `/api/search/hotspots` | 检测新热点 |
| GET | `/api/search/health` | 健康检查 |

## 🔧 功能特性

### 核心功能
- ✅ 搜索功能（调用百度搜索）
- ✅ 搜索记录收集
- ✅ 热门搜索排行
- ✅ 搜索趋势可视化
- ✅ 热点检测（增长率监控）

### 数据统计
- 实时搜索趋势图（ECharts）
- 热门关键词排行
- 最近搜索记录
- 热点增长检测

## 🛠️ 扩展功能

可以添加的功能：

1. **用户登录** - 记录用户搜索历史
2. **搜索建议** - 基于热门的智能提示
3. **分类统计** - 按来源、时间段分析
4. **导出报表** - 导出 Excel/PDF
5. **告警通知** - 热点出现时发送通知

## 📝 配置说明

### 后端配置 (application.yml)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/search_engine
    username: root
    password: root123

search:
  hot-keywords:
    top-count: 10    # 热门词数量
    days: 7          # 统计天数
```

### 前端配置

在 `vite.config.js` 中配置后端代理：

```js
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true
  }
}
```

## 📦 生产部署

### 后端

```bash
# 构建
cd backend
./mvnw package -DskipTests

# 部署
java -jar target/search-engine-1.0.0.jar \
  --spring.profiles.active=prod \
  --spring.datasource.url=jdbc:mysql://prod-db:3306/search_engine
```

### 前端

```bash
cd frontend
npm run build

# nginx 配置
server {
    listen 80;
    root /path/to/dist;
    location /api {
        proxy_pass http://localhost:8080;
    }
}
```

## 📄 License

MIT License
