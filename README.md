# 📦 KMicro – Microservices Ecommerce Platform
![GitHub Repo Size](https://img.shields.io/github/repo-size/krprod/kmicro)  ![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/krprod/kmicro/maven.yml?branch=main)  ![License](https://img.shields.io/github/license/krprod/kmicro)  ![Visitors](https://komarev.com/ghpvc/?username=krprod&label=Visitor&base=1000&color=brightgreen)

A **Spring Boot based microservices ecommerce backend** — architected to demonstrate a production-ready, scalable microservices ecosystem with API documentation, message-driven flows (Kafka), Redis caching, and more.

---

## 🚀 Overview

This project contains multiple interconnected microservices that together form an ecommerce backend:

| Service | Purpose | Port | API Docs |
|---------|---------|------|----------|
| **ApiGateway** | Validate and Add auth header for other services | `9096` | `/webjars/swagger-ui/index.html` |
| **Product Service** | Handles product & category data | `8080` | `/swagger-ui/`, `/springwolf/asyncapi-ui.html`|
| **Order Service** | Manages orders & carts | `8091` | `/swagger-ui/`, `/springwolf/asyncapi-ui.html` |
| **User Service** | Authentication & users | `8085` | `/swagger-ui/`, `/springwolf/asyncapi-ui.html` |
| **Payment Service** | Payment processing | `8095` | `/swagger-ui/`, `/springwolf/asyncapi-ui.html` |
| **Notification Service** | Sends notifications | `8096` | `/swagger-ui/`, `/springwolf/asyncapi-ui.html` |

Each service runs independently using Spring Boot and provides Swagger UI for API exploration.

📑 [Common API Documentation - http://localhost:9096/webjars/swagger-ui/index.html](http://localhost:9096/webjars/swagger-ui/index.html)

📑 [Common Kafka Documentation - http://localhost:9096/my-service/springwolf/asyncapi-ui.html](http://localhost:9096/user-service/springwolf/asyncapi-ui.html)

## 💻 Frontend Repo
[kmicro-frontend - https://github.com/krprod/kmicro-frontend](https://github.com/krprod/kmicro-frontend)

## 📦 Architecture

This system integrates:
- **Spring Cloud** & **Spring Cloud Reactive Gateway** for app entrypoint
- **Spring Boot** & **Spring MVC**
- **Spring Data JPA**
- **Swagger/OpenAPI Docs** for rest enpoint documentation
- **SpringWolf** for events documentation
- **Apache Kafka** for asynchronous communication
- **Redis** for caching
- **LocalStack** for AWS service emulation
- **PostgreSQL** as the primary database
- **React/Redux** for UI
- **Docker** for containerization
- **Grafana** for logging, tracing, and app metrics

## 📢 Coming Soon
  - **Kubernetes/HELM** for deployment
  - **GitOps** for CI/CD workflow
---

## 📌 Setup & Run
### ⬇️ Clone the repository
```bash
git clone https://github.com/krprod/kmicro.git
cd kmicro
mvn spring-boot:run // run any service
```
---
# Commands:
__Docker__
- Requires Docker compose version 2 or greater
```bash
docker compose up -d --force-recreate # recreate all in compose file
docker compose --env-file .env.local up -d --force-recreate user-service # recreate specific service 
docker compose --env-file .env.local up -d --build --force-recreate frontend-service # recreate from dockerfile
```

__Maven__
```bash
mvn compile jib:dockerBuild # create service image for local system
```

__Postgres__
```bash
psql -U <user> -d <db>	 --Connect to a specific database as a user.
psql -h <host> -p <port> -U <user> -d <db>	--Connect to a remote host.
psql -U postgres	--Connect as superuser.
\c <database>	--Switch connection to a new database.
\list --List all databases.
\dn  --List of schemas
\x	--Toggle expanded display mode (vertical output).
\o <file>	--Send query results to a file.
\copy ...	--Copy data between table and CSV file.
\timing	--Toggle query execution time display.
\q	--Quit psql.
\i <file>	Execute SQL commands from a file.
```

__Kafka__
- Kafka UI tool: [KafBat Ui - ](https://ui.docs.kafbat.io/)

# Services
__Product Service__
- URL: http://localhost:8080/
  - Product Serivce Endpoint: /api/products
  - Category Serivce Endpoint: /api/category
- DOC: http://localhost:8080/swagger-ui/index.html | http://localhost:8080/v3/api-docs

__Order Service__
- URL: http://localhost:8091/ 
  - Cart Service Endpoint: /api/carts
  - Order Service Endpoint: /api/orders
- DOC: http://localhost:8091/swagger-ui/index.html | http://localhost:8091/v3/api-docs
- - Kafka Doc: http://localhost:8091/springwolf/asyncapi-ui.html#info | http://localhost:8091/springwolf/docs
 
__User Service__
- URL: http://localhost:8085/api/
  - Auth Service Endpoint: /api/auth
  - Users Service Endpoint: /api/users
- DOC: http://localhost:8085/swagger-ui/index.html | http://localhost:8085/v3/api-docs
- Kafka Doc: http://localhost:8085/springwolf/asyncapi-ui.html#info | http://localhost:8085/springwolf/docs

__Notification Service__
- URL: http://localhost:8096/api/notifications
- DOC: http://localhost:8096/swagger-ui/index.html | http://localhost:8096/v3/api-docs 
- Kafka Doc: http://localhost:8096/springwolf/asyncapi-ui.html#info | http://localhost:8096/springwolf/docs
