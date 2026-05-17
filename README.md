# zefflix-common

Bütün Zefflix mikroservislər üçün paylaşılan kitabxana.

## Məzmun

| Paket | Məzmun |
|---|---|
| `exception` | `BaseException`, `ResourceNotFoundException`, `ConflictException`, `BadRequestException`, `UnauthorizedException`, `ForbiddenException`, `GlobalExceptionHandler` |
| `response` | `ApiResponse<T>` – bütün API cavabları üçün standart wrapper |
| `pagination` | `PageResponse<T>` – sayfalı siyahı cavabları |
| `audit` | `BaseEntity` – JPA audit (createdAt, updatedAt, createdBy, updatedBy) |
| `security` | `JwtUtil`, `UserPrincipal`, `SecurityUtils` |
| `constants` | `KafkaTopics`, `SecurityConstants` |

## Servisinizə necə əlavə etmək

### 1. `build.gradle.kts`-ə `repositories` əlavə edin:
```kotlin
repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/zefflix/zefflix-common")
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}
```

### 2. Dependency əlavə edin:
```kotlin
implementation("az.zefflix:zefflix-common:1.0.0")
```

### 3. JWT konfiqurasiyasını `application.yml`-ə əlavə edin:
```yaml
zefflix:
  jwt:
    secret: "your-256-bit-secret-key-change-in-production"
    access-token-expiration: 900000      # 15 dəq
    refresh-token-expiration: 604800000  # 7 gün
```

## İstifadə nümunələri

### Controller-də ApiResponse:
```java
@GetMapping("/{id}")
public ResponseEntity<ApiResponse<ContentDto>> getContent(@PathVariable UUID id) {
    ContentDto dto = contentService.findById(id);
    return ResponseEntity.ok(ApiResponse.success("Məzmun tapıldı.", dto));
}
```

### Cari istifadəçini almaq:
```java
UUID userId = SecurityUtils.getCurrentUserId();
UserPrincipal user = SecurityUtils.getCurrentUser();
```

### Exception atmaq:
```java
// 404
throw new ResourceNotFoundException("Film", "id", filmId);

// 409 (email artıq mövcuddur)
throw new ConflictException("User", "email", email);

// 400
throw new BadRequestException("password", "minimum 8 simvol olmalıdır");
```

### Sayfalı cavab:
```java
Page<Content> page = contentRepository.findAll(pageable);
return ResponseEntity.ok(
    ApiResponse.success(PageResponse.from(page.map(mapper::toDto)))
);
```

## Yeni versiya buraxmaq (pipeline)

```bash
git tag v1.1.0
git push origin v1.1.0
```

GitHub Actions avtomatik:
1. Build & test keçirir
2. Checkstyle yoxlayır
3. GitHub Packages-ə publish edir
4. GitHub Release yaradır
