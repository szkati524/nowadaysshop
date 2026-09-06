E-Commerce System Documentation (Microservices Architecture)
Architecture Diagram
flowchart TD
    subgraph Client ["Client"]
        ReactUI["React Frontend (Vite)<br/>Port: 5173"]
    end

    subgraph Infrastructure ["Server Infrastructure / Gateway"]
        Gateway["API Gateway (Spring Cloud Gateway)<br/>Port: 8083"]
        Eureka["Discovery Server (Eureka)<br/>Port: 8761"]
    end

    subgraph Microservices ["Backend Microservices"]
        UserService["User Service<br/>Port: 8082"]
        CatalogService["Catalog Service<br/>Port: 8086"]
        OrderService["Order Service<br/>Port: 8088"]
        NotificationService["Notification Service<br/>Port: 8089"]
    end

    subgraph Databases ["Databases"]
        PostgresUser[("PostgreSQL: user_db<br/>Port: 5433 (5432)")]
        PostgresCatalog[("PostgreSQL: catalog_db<br/>Port: 5433 (5432)")]
        Cassandra[("Cassandra DB: ShopCluster<br/>Port: 9042")]
    end

    subgraph Messaging ["Asynchronous Messaging"]
        RabbitMQ{{"RabbitMQ Broker<br/>Port: 5672 / 15672"}}
    end

    subgraph Observability ["Observability Stack"]
        Prometheus["Prometheus<br/>Port: 9090"]
        Promtail["Promtail<br/>Port: 9080"]
        Loki["Grafana Loki<br/>Port: 3100"]
        Grafana["Grafana Dashboard<br/>Port: 3000"]
    end

    %% HTTP / REST Requests Flow
    ReactUI -->|HTTP / REST| Gateway
    Gateway -->|Registration/Discovery| Eureka
    UserService -->|Registration/Discovery| Eureka
    CatalogService -->|Registration/Discovery| Eureka
    OrderService -->|Registration/Discovery| Eureka
    NotificationService -->|Registration/Discovery| Eureka

    Gateway -->|Routing / REST| UserService
    Gateway -->|Routing / REST| CatalogService
    Gateway -->|Routing / REST| OrderService

    %% Synchronous Inter-service Communication
    OrderService -->|Synch. REST| CatalogService
    OrderService -->|Synch. REST| UserService

    %% Databases
    UserService --> PostgresUser
    CatalogService --> PostgresCatalog
    OrderService --> Cassandra

    %% Event-Driven Messaging
    UserService -->|Event Publishing| RabbitMQ
    OrderService -->|Event Publishing| RabbitMQ
    RabbitMQ -->|Event Consumption| NotificationService

    %% Observability & Metrics
    UserService -.->|/actuator/prometheus| Prometheus
    CatalogService -.->|/actuator/prometheus| Prometheus
    OrderService -.->|/actuator/prometheus| Prometheus
    NotificationService -.->|/actuator/prometheus| Prometheus
    Eureka -.->|/actuator/prometheus| Prometheus

    Prometheus --> Grafana
    Promtail -->|Docker Logs| Loki
    Loki --> Grafana

    Architecture & SecurityHexagonal Architecture & Domain-Driven Design (DDD)Each domain microservice is designed following Hexagonal Architecture (Ports and Adapters) and DDD principles:
      Domain Layer: Contains pure business logic (aggregates, value objects, domain exceptions, and domain services) without external framework dependencies (free of Spring/JPA annotations). 
       Application Layer: Defines Use Cases and In/Out Ports driving data flow.
         Infrastructure Layer: Contains Adapters — REST Controllers (Input Adapters), JPA/Cassandra entities, Spring Data repositories, and REST clients (Output Adapters). 
          Stateless JWT AuthorizationToken Acquisition: The user authenticates in user-service via /api/v1/auth/login and receives a cryptographically signed JWT.  
          API Gateway (Stateless Routing): The API Gateway receives the request with the Authorization: Bearer <token> header. It validates the JWT signature and expiration statelessly without querying the database.
            Context Propagation: Validated user roles and identity are forwarded statelessly via HTTP headers to downstream microservices (catalog-service, order-service), where dedicated security filters (JwtAuthenticationFilter) process the context.  Summary of System Microservices and ComponentsService / ComponentPortDatabase / ReliabilityRole in SystemAPI Gateway8083None (Stateless)Single entrypoint (Reverse Proxy), request routing, and JWT verification.
              Discovery Server (Eureka)8761None (In-Memory)Service Registry for dynamic microservice lookup. 
               User Service8082PostgreSQL (user_db)Account management, Wallet, registration, and authentication. 
                Catalog Service8086PostgreSQL (catalog_db)Product catalog, search, and inventory management.  Order Service8088Cassandra DB (ShopCluster)Order creation and management (high availability CQL writes).
                  Notification Service8089None (Event-Driven)Asynchronous RabbitMQ event consumer (sending emails/push notifications). 
                   React Frontend5173Browser StorageSingle Page Application (SPA) built with React + Vite + Tailwind CSS.  RabbitMQ5672 / 15672Disk / Memory QueuesMessage broker for asynchronous event-driven communication. 
                    PostgreSQL5433 (5432)Relational DBPersistent storage for user transactions and product catalog.  Cassandra9042NoSQL Column-StoreDistributed database optimized for high-volume order writes.  Prometheus9090TSDB (Time Series)Collects application metrics from /actuator/prometheus endpoints. 
                     Grafana3000SQLite (Internal)Visualizes metrics and log dashboards. 
                      Loki & Promtail3100 / 9080Log StoreCollects Docker logs (Promtail) and indexes them in Loki. 
                       CI/CD & Testing (Jenkins Pipeline)The Jenkinsfile pipeline automates the complete build and test lifecycle: 
                        Checkout Source: Pulls source code from the Git repository.  Backend - Unit & Integration Tests: Runs mvn clean test. Integration tests use Testcontainers to automatically spin up temporary Docker containers for dependencies like databases and RabbitMQ.
                          Frontend - Unit Tests: Installs dependencies (npm ci) and runs fast unit tests using Vitest (npm run test:run). 
                           Build Docker Images: Compiles backend images via Spring Boot plugin (mvn spring-boot:build-image) and builds the frontend Docker image. 
                            E2E Tests (Playwright): Starts the environment using docker compose up -d --build, then executes end-to-end tests (e.g., checkout flows) via Playwright (npx playwright test). Cleans up with docker compose down -v and archives Playwright test artifacts.  Deployment & Setup Guide1. PrerequisitesDocker and Docker Compose installed.  Java 17+ and Node.js 20+ installed (if running services locally outside Docker).  2. Running ServicesClone the repository and run the following command in the project root:  

    docker compose up -d

    To rebuild images after code changes:  

    docker compose up -d --build

    To shut down containers and clear volume data:

    docker compose down -v

    3. Application Endpoints & Dashboard URLs  Once all containers are running, you can access the services at the following URLs:
      React Frontend App: http://localhost:5173  
      Eureka Discovery Server Dashboard: http://localhost:8761  
      API Gateway Entrypoint: http://localhost:8083 
       RabbitMQ Management Dashboard: http://localhost:15672 (Default login: guest / guest)
         Grafana Dashboards: http://localhost:3000 (Default login: admin / admin)  
         Prometheus Metrics Web UI: http://localhost:9090 
          PgAdmin (PostgreSQL GUI): http://localhost:5050 (Default login: admin@shop.com / admin)