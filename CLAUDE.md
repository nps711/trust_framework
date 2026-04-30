# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Java 17 Maven multi-module project for a trust/quantitative trading business technology platform. It uses Spring Boot 3.5.13 + Spring Cloud 2025.0.2 with a "client SDK + independent service" architecture pattern common in Chinese financial tech mid-platforms.

## Build Commands

No Maven wrapper is present. Use system `mvn`.

```bash
# Build all modules from repository root
mvn clean install

# Build a single module
cd trust-quant-trade-service && mvn clean install

# Skip tests
mvn clean install -DskipTests

# Run a specific service
cd trust-quant-trade-service && mvn spring-boot:run
```

## Module Architecture (3 Layers)

### Layer 1: Build Skeleton
- `trust-cloud-parent` — Parent POM. Defines Maven plugins, Java 17 compiler, UTF-8 encoding. **All services must inherit this.** Does NOT manage dependency versions.
- `trust-cloud-bom` — BOM POM. Single source of truth for all dependency versions (Spring Boot, Spring Cloud, MyBatis Plus, Apollo, Redisson, Hutool, and internal common modules). Parent imports this. **Never add `<version>` tags in child POMs.**

### Layer 2: Common Components (`trust-common-*`)

**Pure utility modules** (no corresponding server):
- `trust-common-core` — `R<T>` unified response, `BusinessException`, `UserContextHolder` (TTL-based), Snowflake ID generator, global constants. **All other modules depend on this.**
- `trust-common-web` — WebMvc config, Knife4j/Swagger auto-config, global exception handler, XSS filter, `TrustContextFilter` (injects headers into TTL at web entry). Also configures Jackson to serialize `Long` and `BigDecimal` as Strings to prevent JS precision loss.
- `trust-common-db` — MyBatis Plus config, Flyway migration, pagination plugin, `BlockAttackInnerInterceptor` (prevents full-table update/delete), multi-datasource support, `MetaObjectHandler` (auto-fills create/update fields).
- `trust-common-log` — Logback template, SkyWalking traceID integration, `@AuditLog` AOP aspect for audit logging.
- `trust-common-config` — Apollo client encapsulation, default namespace injection, dynamic config refresh via `@RefreshScope`, Jasypt encrypted config support.

**Client SDK modules** (paired with independent servers):
- `trust-common-security` — Spring Security filter chain, `@DataScope` annotation, data-permission MyBatis interceptor (auto-injects `WHERE dept_id IN (...)`), JWT/token validation. Paired with `trust-auth-service` (not yet created).
- `trust-common-file` — `OssTemplate` abstraction for MinIO/S3 upload/download/preview. Paired with `trust-file-service` (not yet created).
- `trust-common-mq` — RabbitMQ JSON serialization, reliable delivery (`ReliableProducer` with local message table pattern), `ContextMessagePostProcessor` (injects TTL context into message headers), `RabbitListenerAspect` (restores TTL context on consume).
- `trust-common-redis` — `RedisTemplate` JSON serialization, Redisson distributed lock (`@DistLock`), cache utilities.
- `trust-common-rpc` — OpenFeign config, `ContextFeignInterceptor` (propagates `x-user-id` and `x-trace-id` headers to downstream services), load balancing, circuit breaker templates.

### Layer 3: Business Services
- `trust-gateway-service` — Spring Cloud Gateway. Entry point, routing, auth pre-check. Currently routes `/quant/**` to trade service.
- `trust-quant-account-service` — Quant account service. Account opening, asset query, fee calculation, position query.
- `trust-quant-trade-service` — **Trade orchestrator.** Aggregates account + quotation services via Feign. Read ops use sync Feign (3s timeout), write ops use async MQ for eventual consistency.

## Critical Architecture: Full-Link Context Propagation

The `UserContext` (containing `userId`, `traceId`) must propagate through **HTTP → ThreadPool → Feign → MQ** without loss. This is the project's most important cross-file mechanism.

1. **Web entry** (`trust-common-web`): `TrustContextFilter` (Ordered.HIGHEST_PRECEDENCE) reads `x-user-id` / `x-trace-id` from HTTP headers and injects into `UserContextHolder` (TTL). **Must clear in `finally` block** to prevent Tomcat thread reuse causing context leakage.
2. **Thread pool** (`trust-common-core`): Spring's default `asyncExecutor` is overridden with `@Primary` and wrapped by `TtlExecutors.getTtlExecutor(...)`, so `@Async` threads inherit context automatically.
3. **Feign outbound** (`trust-common-rpc`): `ContextFeignInterceptor` reads from TTL and writes headers to downstream requests.
4. **MQ producer** (`trust-common-mq`): `ContextMessagePostProcessor` serializes context into RabbitMQ message properties headers.
5. **MQ consumer** (`trust-common-mq`): `RabbitListenerAspect` restores context from message headers into TTL before `@RabbitListener` method runs, and **clears in `finally`**.

**Rule**: Never use JDK native `ThreadLocal`. Always use `TransmittableThreadLocal` via `UserContextHolder`.

## API & Interaction Conventions

These are **mandatory** and enforced by architecture governance:

- **All APIs must be POST + JSON.** No GET, PUT, DELETE. No URL parameters (`@RequestParam` is forbidden). No path variables.
- All requests must extend `BaseRequest`.
- All responses must use `R<T>` (code, msg, data, traceId).
- Long IDs (Snowflake, 19 digits) and `BigDecimal` amounts are serialized as Strings in JSON to prevent frontend JS precision truncation.
- Money fields use `DECIMAL(20, 6)` in DB. Never use Float/Double.
- IDs are generated by Snowflake (19-bit Long). Never use auto-increment.

## Dependency & Governance Rules

- **Never add `<version>` tags** for dependencies managed by BOM (Spring Boot, Hutool, MyBatis Plus, internal common modules, etc.).
- **Never create private utility classes** in business services. If `trust-common-core` lacks a utility, add it there via PR.
- **Never directly use raw middleware**: no native JDBC, no raw `RedisTemplate` (use `trust-common-redis`), no raw RabbitMQ channel (use `trust-common-mq`), no direct K8s API calls.
- **Never directly depend on Nacos** — service discovery uses Spring Cloud Kubernetes (K8s DNS), not Nacos.
- Business services depend on common modules **on demand**:
  - Standard business service (DB + API + auth): `trust-common-web`, `trust-common-security`, `trust-common-db`, `trust-common-rpc`, `trust-common-log`, `trust-common-config`
  - Simple tool service: just `trust-common-core`

## Database & Migration

- Database: PostgreSQL 15+ (not MySQL).
- Schema migrations: **Flyway**. Scripts follow `V1.0.1__description.sql` naming and live in `src/main/resources/db/migration`.
- Modeling: PDManer or Chiner. Model files committed to Git.
- Table and column comments are mandatory; CI checks enforce this.

## Key Configuration Prefixes

- `trust.security.*` — Security module config (ignore URLs, etc.)
- `trust.swagger.*` — Swagger/Knife4j doc config
- Apollo namespaces: `application`, `TEST.common` are auto-injected by `trust-common-config`

## Creating a New Business Service

1. Create new module under repository root.
2. Inherit `trust-cloud-parent` as `<parent>`.
3. Import `trust-cloud-bom` in `<dependencyManagement>` with `<scope>import</scope>`.
4. Add required `trust-common-*` dependencies **without version tags**.
5. Add `spring-boot-maven-plugin` in `<build><plugins>`.
6. Create `application.yml` with `spring.application.name`.
7. Main class: standard `@SpringBootApplication`.
8. Package structure follows DDD-ish convention: `api/`, `application/`, `domain/`, `infrastructure/`.
