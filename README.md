# ✦ Our Soial Feeds — Backend

A Spring Boot 3.5 REST API powering the Our Soial Feeds social media app. Handles user authentication (JWT), posts with media uploads, likes, comments, and serves uploaded files — all containerized with Docker.

---

## 🗂 Project Structure

```
src/main/java/com/example/demo/
├── config/
│   ├── CorsConfig.java          # Global CORS (allows all origins for dev)
│   ├── JwtFilter.java           # JWT request filter (validates Bearer token)
│   ├── LoggingFilter.java       # HTTP request logging
│   ├── SecurityConfig.java      # Spring Security — stateless, JWT-based
│   └── WebConfig.java           # Serves /uploads/** from /app/uploads/ on disk
├── controller/
│   ├── AuthController.java      # POST /auth/register, POST /auth/login
│   ├── PostController.java      # CRUD posts + file upload
│   ├── CommentController.java   # GET/POST comments per post
│   ├── LikeController.java      # Toggle likes, get like count
│   ├── FeedController.java      # Aggregated feed response
│   └── UserController.java      # GET /users/me
├── dto/
│   └── FeedResponse.java
├── entity/
│   ├── User.java
│   ├── Post.java
│   ├── Comment.java
│   └── Like.java
├── repository/
│   ├── UserRepository.java
│   ├── PostRepository.java
│   ├── CommentRepository.java
│   └── LikeRepository.java
├── util/
│   └── JwtUtil.java             # Token generation and extraction
└── DemoApplication.java
```

---

## ⚙️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| Database | MySQL 8 |
| ORM | Spring Data JPA / Hibernate |
| Build Tool | Gradle |
| Container | Docker + Docker Compose |
| Tunnel | Cloudflare Tunnel (for public internet access) |

---

## 🔌 API Endpoints

All endpoints except `/auth/**` and `/uploads/**` require the header:
```
Authorization: Bearer <token>
```

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/auth/test` | ❌ | Health check |
| `POST` | `/auth/register` | ❌ | Register new user |
| `POST` | `/auth/login` | ❌ | Login, returns JWT token |
| `GET` | `/users/me` | ✅ | Get current user info |
| `GET` | `/posts` | ✅ | Get all posts (with like counts + media URLs) |
| `POST` | `/posts/upload` | ✅ | Create post with optional media files |
| `DELETE` | `/posts/{postId}` | ✅ | Delete own post (also removes media files) |
| `GET` | `/comments?postId=` | ✅ | Get comments for a post |
| `POST` | `/comments?postId=&text=` | ✅ | Add a comment |
| `POST` | `/likes?postId=` | ✅ | Toggle like on a post |
| `GET` | `/likes/count?postId=` | ✅ | Get like count for a post |
| `GET` | `/uploads/{filename}` | ❌ | Serve uploaded media files |

---

## 🖼 Media Upload

- Uploaded files are saved to `/app/uploads/` inside the container (mapped to a Docker volume for persistence).
- The `app.base-url` property in `application.properties` is prepended to filenames when returned to the frontend, forming full URLs like `https://your-domain.com/uploads/filename.jpg`.
- Supports up to **200MB** per file and per request.

---

## 🔐 Security Notes

- Passwords are hashed with **BCrypt** before storing.
- JWT tokens are signed with the secret in `application.properties` (`jwt.secret`).
- Change `jwt.secret` to a strong random string before going to production.
- SQL logging is disabled in production to prevent data exposure.

---

## 🛠 Prerequisites

- Java 21 (for local builds)
- Gradle (wrapper included — use `./gradlew`)
- Docker & Docker Compose
- Cloudflare Tunnel (`cloudflared`) — for exposing to the internet

---

## 📦 Building the JAR

Before building the Docker image, you need to compile and package the app into a JAR.

```bash
# From the project root (where build.gradle is)
./gradlew clean build -x test
```

This produces:
```
build/libs/demo-0.0.1-SNAPSHOT.jar
```

> The `-x test` flag skips tests so the build doesn't fail if the DB isn't running locally.

---

## 🐳 Docker Setup

### First-time Setup — Build and Start Everything

```bash
# 1. Build the JAR first (required before building the image)
./gradlew clean build -x test

# 2. Build the Docker image and start all containers
docker-compose up --build -d
```

This starts two containers:
- `mysql-container` — MySQL 8 on port `3307` (host) → `3306` (container)
- `social-app-container` — Spring Boot app on port `8080`

Both use named Docker volumes so data persists across restarts:
- `mysql-data` — database files
- `uploads-data` — uploaded images and videos

---

### Rebuilding After Code Changes

When you change the source code, you need to rebuild the JAR and recreate the app container. The database container and its volume are left untouched.

```bash
# Step 1 — Rebuild the JAR
./gradlew clean build -x test

# Step 2 — Stop and remove only the app container (keep DB running)
docker stop social-app-container
docker rm social-app-container

# Step 3 — Rebuild the Docker image
docker-compose build app

# Step 4 — Start the new app container (reuses existing volumes)
docker-compose up -d app
```

> The `uploads-data` and `mysql-data` volumes are NOT deleted in this process — all your data and uploaded files are preserved.

---

### Stopping Everything

```bash
docker-compose down
```

> This stops and removes containers but **keeps the volumes**. Your data is safe.

To also delete all data (full reset):

```bash
docker-compose down -v
```

---

### Checking Logs

```bash
# App logs
docker logs social-app-container -f

# MySQL logs
docker logs mysql-container -f
```

---

### Useful Docker Commands

```bash
# See running containers
docker ps

# List volumes
docker volume ls

# Inspect a volume (find its mount path)
docker volume inspect social_app_uploads-data

# Shell into the app container
docker exec -it social-app-container bash

# Shell into MySQL
docker exec -it mysql-container mysql -u root -p1234 social_app
```

---

## 🌐 Exposing to the Internet with Cloudflare Tunnel

The app runs locally on port `8080`. To access it from the internet without opening router ports, use **Cloudflare Tunnel**.

### Install Cloudflare Tunnel

```bash
# macOS
brew install cloudflare/cloudflare/cloudflared

# Linux (Debian/Ubuntu)
curl -L https://pkg.cloudflare.com/cloudflare-main.gpg | sudo apt-key add -
echo "deb https://pkg.cloudflare.com/ $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/cloudflare.list
sudo apt update && sudo apt install cloudflared

# Windows
winget install -e --id Cloudflare.cloudflared

```

### Start a Tunnel (Quick / Temporary)

```bash
# Expose backend
cloudflared tunnel --url http://localhost:8080

# Expose frontend (if running locally e.g. on port 3000)
cloudflared tunnel --url http://localhost:3000
```

Cloudflare will print a public URL like `https://random-name.trycloudflare.com`. Use this as your domain.

---

## 🔧 Updating the Domain in application.properties

The `app.base-url` property controls the base URL that gets prepended to all media file URLs returned by the API. **You must update this whenever your Cloudflare tunnel URL changes.**

```properties
# application.properties

# For local development
app.base-url=http://localhost:8080

# For production (replace with your actual Cloudflare tunnel URL)
app.base-url=https://your-tunnel-name.trycloudflare.com
```

After changing this:

```bash
# Rebuild the JAR with the new config
./gradlew clean build -x test

# Recreate the app container
docker stop social-app-container
docker rm social-app-container
docker-compose build app
docker-compose up -d app
```

---

## 🔧 Updating the Frontend BASE_URL

In `App.jsx` (frontend), update `BASE_URL` to match the backend's Cloudflare tunnel URL:

```js
const BASE_URL = "https://your-backend-tunnel.trycloudflare.com";
```

And update the allowed CORS origins in `SecurityConfig.java` to include your frontend URL:

```java
config.setAllowedOrigins(List.of(
    "http://localhost:3000",
    "https://your-frontend-tunnel.trycloudflare.com"   // ← add this
));
```

Then rebuild and redeploy the backend after making this change.

---

## 🗄 Database Configuration

The `docker-compose.yml` sets these environment variables for the app container at runtime — they override `application.properties`:

| Variable | Value |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://mysql:3306/social_app` |
| `SPRING_DATASOURCE_USERNAME` | `root` |
| `SPRING_DATASOURCE_PASSWORD` | `1234` |

MySQL is exposed to your host machine on port `3307` (to avoid conflicts if you have a local MySQL running on `3306`). Connect with any MySQL client:

```
Host: localhost
Port: 3307
User: root
Password: 1234
Database: social_app
```

---

## 📋 application.properties Reference

```properties
# Base URL used to build media file URLs returned to the frontend
app.base-url=http://localhost:8080

# JWT secret — change to a strong random string for production
jwt.secret=V7rK9mP2xQ4wZ6nL8sY1uA3dF5hJ0cE

# File upload limits
spring.servlet.multipart.max-file-size=200MB
spring.servlet.multipart.max-request-size=200MB

# DB connection timeouts (ms) — useful when app starts before DB is ready
spring.datasource.hikari.initializationFailTimeout=60000
spring.datasource.hikari.connectionTimeout=60000

# Auto create/update tables from entity classes
spring.jpa.hibernate.ddl-auto=update
```

---

## 🤝 Contributing

1. Fork the repo
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Build and test locally
4. Commit: `git commit -m "Add your feature"`
5. Push and open a Pull Request

---

## 📄 License

MIT — free to use and modify.

