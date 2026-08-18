Aptio - Smart Appointment — System Architecture

## Overview

SlotSync follows a **clean layered architecture** with strict dependency rules:

```
Controller → Service (interface) → Repository → Domain Entity
                ↑
            DTO ↔ Mapper (MapStruct)
```

No layer may skip a layer below it. Entities are never exposed directly in API responses — all data flows through DTOs.

---

## High-Level Architecture Diagram

```mermaid
graph TB
    subgraph Client["Client (Browser)"]
        React["React 18 + TypeScript\nVite + TanStack Query"]
    end

    subgraph Proxy["Reverse Proxy"]
        Nginx["Nginx 1.27\nLoad Balancer + SSL"]
    end

    subgraph Backend["Spring Boot 3 API"]
        direction TB
        Controller["REST Controllers\n/api/v1/**"]
        Security["Spring Security\nJWT Filter + RBAC"]
        Service["Service Layer\nBusiness Logic"]
        Event["Spring Events\n@Async EventListener"]
        WS["WebSocket STOMP\n/ws endpoint"]
        Sched["@Scheduled\nSlot Generator"]
        Repo["JPA Repositories\n+ Specifications"]
        Mapper["MapStruct\nEntity ↔ DTO"]
    end

    subgraph Data["Data Layer"]
        MySQL["MySQL 8\nPrimary Database"]
    end

    subgraph Infra["Infrastructure"]
        Docker["Docker Compose\nContainer Orchestration"]
        GHA["GitHub Actions\nCI/CD Pipeline"]
    end

    Client -->|"HTTPS"| Nginx
    Nginx -->|"/api/**"| Controller
    Nginx -->|"/ws/**"| WS
    Security --> Controller
    Controller --> Service
    Service --> Event
    Service --> Repo
    Service --> Mapper
    Event -->|"@Async"| Service
    WS -->|"Broadcast"| Client
    Repo --> MySQL
    Docker --> Backend
    Docker --> MySQL
    GHA --> Docker
```

---

## Backend Package Structure

```
com.slotsync.backend/
│
├── config/                     
│   ├── AppConfig.java          
│   ├── AsyncConfig.java     
│   ├── CorsConfig.java        
│   ├── OpenApiConfig.java     
│   ├── SecurityConfig.java     
│   └── WebSocketConfig.java   
│
├── controller/                 
│   ├── AuthController.java
│   ├── BookingController.java
│   ├── ProviderController.java
│   ├── SlotController.java
│   ├── WaitlistController.java
│   ├── NotificationController.java
│   └── AdminController.java
│
├── service/                   
│   ├── AuthService.java
│   ├── BookingService.java
│   ├── ProviderService.java
│   ├── SlotService.java
│   ├── WaitlistService.java
│   ├── NotificationService.java
│   ├── EmailService.java
│   └── AdminService.java
│
├── service/impl/             
│   ├── AuthServiceImpl.java
│   ├── BookingServiceImpl.java  ← Core: @Transactional + pessimistic lock
│   ├── ProviderServiceImpl.java
│   ├── SlotServiceImpl.java
│   ├── WaitlistServiceImpl.java
│   ├── NotificationServiceImpl.java
│   ├── EmailServiceImpl.java    ← Graceful SMTP fallback
│   └── AdminServiceImpl.java
│
├── repository/                 # Spring Data JPA repositories
│   ├── UserRepository.java
│   ├── ProviderRepository.java
│   ├── SlotRepository.java      ← findByIdWithLock() PESSIMISTIC_WRITE
│   ├── BookingRepository.java
│   ├── WaitlistRepository.java
│   ├── NotificationRepository.java
│   ├── RefreshTokenRepository.java
│   └── AuditLogRepository.java
│
├── domain/                     # JPA entities (never exposed in API responses)
│   ├── User.java
│   ├── Provider.java
│   ├── Slot.java               ← @Version for optimistic locking
│   ├── Booking.java
│   ├── Waitlist.java
│   ├── Notification.java
│   ├── RefreshToken.java
│   └── AuditLog.java
│
├── dto/
│   ├── request/              
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   ├── CreateSlotRequest.java
│   │   ├── BookingRequest.java
│   │   └── ...
│   └── response/              
│       ├── AuthResponse.java
│       ├── SlotResponse.java
│       ├── BookingResponse.java
│       ├── PagedResponse.java   ← Generic paginated wrapper
│       └── ...
│
├── mapper/                    
│   ├── UserMapper.java
│   ├── ProviderMapper.java
│   ├── SlotMapper.java
│   ├── BookingMapper.java
│   └── NotificationMapper.java
│
├── event/                     
│   ├── BookingConfirmedEvent.java
│   ├── BookingCancelledEvent.java
│   └── WaitlistPromotedEvent.java
│
├── exception/                 
│   ├── GlobalExceptionHandler.java   ← @RestControllerAdvice
│   ├── ResourceNotFoundException.java
│   ├── SlotNotAvailableException.java ← Maps to 409 Conflict
│   ├── DuplicateBookingException.java
│   ├── UnauthorizedException.java
│   └── ValidationException.java
│
├── security/                  
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   └── UserDetailsServiceImpl.java
│
├── scheduler/                
│   ├── RecurringSlotGeneratorJob.java
│   └── WaitlistCleanupJob.java
│
├── websocket/                
│   └── SlotBroadcastService.java
│
├── specification/             
│   ├── ProviderSpecification.java
│   └── BookingSpecification.java
│
└── validation/                
    ├── FutureOrPresent.java
    └── ValidTimeRange.java
```

---

## Security Architecture

```mermaid
sequenceDiagram
    participant C as Client
    participant F as JWT Filter
    participant S as Security Context
    participant API as Controller

    C->>F: Request with Authorization: Bearer <token>
    F->>F: Extract JWT from header
    F->>F: Validate signature + expiry
    alt Valid Token
        F->>S: Set Authentication in SecurityContext
        S->>API: Request continues to controller
        API-->>C: 200 Response
    else Invalid / Expired Token
        F-->>C: 401 Unauthorized
    end
```

---

## Design Principles Applied

| Principle | Implementation |
|-----------|---------------|
| **Single Responsibility** | Each class has one reason to change (controllers only route, services only execute business logic) |
| **Open/Closed** | `BookingService` interface is open for extension (e.g., adding payment) without modifying consumers |
| **Liskov Substitution** | `EmailServiceImpl` can be swapped for `MockEmailService` in tests without breaking callers |
| **Interface Segregation** | Separate `BookingService`, `WaitlistService`, `NotificationService` instead of one God service |
| **Dependency Inversion** | All service consumers depend on interfaces, not implementations |
