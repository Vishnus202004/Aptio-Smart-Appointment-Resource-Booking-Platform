# SlotSync — Smart Appointment & Resource Booking Platform

<p align="center">
  <strong>A production-quality, enterprise-level booking system with zero double-booking guarantee.</strong>
</p>

<p align="center">
  <img alt="Java" src="https://img.shields.io/badge/Java-21-orange?logo=openjdk" />
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?logo=spring" />
  <img alt="React" src="https://img.shields.io/badge/React-18-blue?logo=react" />
  <img alt="TypeScript" src="https://img.shields.io/badge/TypeScript-5-blue?logo=typescript" />
  <img alt="MySQL" src="https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql" />
  <img alt="Docker" src="https://img.shields.io/badge/Docker-Compose-blue?logo=docker" />
  <img alt="Coverage" src="https://img.shields.io/badge/Coverage-85%25-brightgreen" />
</p>

---

## What is SlotSync?

SlotSync is a multi-tenant SaaS booking platform where customers can reserve appointment slots offered by providers. Use cases include:

- 🏥 Doctor appointments
- 🏢 Meeting room bookings
- 💼 Coworking desk reservations
- 👨‍💻 Consultation sessions
- 🎪 Event seat reservations

### Key Features

| Feature | Details |
|---------|---------|
| **Zero Double Booking** | Pessimistic/Optimistic locking + DB unique constraints |
| **Real-Time Updates** | WebSocket (STOMP) broadcasts slot availability instantly |
| **FIFO Waitlist** | Automatic promotion when booking is cancelled |
| **Role-Based Access** | ADMIN / PROVIDER / CUSTOMER with method-level security |
| **JWT Auth** | Access token (15min) + Refresh token rotation (7 days) |
| **Async Notifications** | Spring Events + @Async — never blocks booking transaction |
| **Email Notifications** | Optional SMTP — app works without mail server |
| **Analytics Dashboard** | Booking stats, occupancy rates, revenue trends |
| **OpenAPI Docs** | Swagger UI at `/swagger-ui.html` |
| **Audit Logging** | Every critical action is logged with actor + details |

---

## Tech Stack

### Backend
| Technology | Version | Purpose |
|-----------|---------|---------|
| Java | 21 | Language |
| Spring Boot | 3.2 | Framework |
| Spring Security | 6 | Authentication + Authorization |
| Spring Data JPA | 3.2 | Database access |
| Hibernate | 6 | ORM |
| MySQL | 8.0 | Primary database |
| Flyway | Latest | Database migrations |
| JWT (JJWT) | 0.12 | Token-based auth |
| MapStruct | 1.5 | DTO ↔ Entity mapping |
| Lombok | 1.18 | Boilerplate reduction |
| WebSocket STOMP | — | Real-time updates |
| Spring Mail | — | Email notifications |
| SpringDoc OpenAPI | 2.5 | Swagger UI |
| JUnit 5 | — | Unit testing |
| Mockito | — | Mocking |
| JaCoCo | 0.8 | Code coverage (85% target) |
| Testcontainers | 1.19 | Integration tests |

### Frontend
| Technology | Version | Purpose |
|-----------|---------|---------|
| React | 18 | UI framework |
| TypeScript | 5 | Type safety |
| Vite | 5 | Build tool |
| Tailwind CSS | 3 | Utility-first CSS |
| shadcn/ui + Radix | — | Accessible UI primitives |
| TanStack Query | 5 | Server state + caching |
| Axios | 1.7 | HTTP client |
| React Hook Form + Zod | — | Form validation |
| Framer Motion | 11 | Animations |
| Recharts | 2 | Analytics charts |
| FullCalendar | 6 | Booking calendar |
| STOMP.js | 7 | WebSocket client |
| Sonner | 1.5 | Toast notifications |

### DevOps
| Technology | Purpose |
|-----------|---------|
| Docker + Compose | Container orchestration |
| Nginx | Reverse proxy + static file serving |
| GitHub Actions | CI/CD pipeline |
| Trivy | Container security scanning |

---

## Getting Started

### Prerequisites
- Docker & Docker Compose
- Java 21 (for local backend dev)
- Node.js 20 (for local frontend dev)

### Quick Start with Docker

```bash
# Clone the repository
git clone https://github.com/your-org/slotsync.git
cd slotsync

# Copy environment variables
cp .env.example .env
# Edit .env with your values (JWT_SECRET is required!)

# Start all services
docker-compose up --build

# Access:
# App:     http://localhost
# API:     http://localhost/api/v1
# Swagger: http://localhost/swagger-ui.html
# MySQL:   localhost:3306
```

### Local Development

**Backend:**
```bash
cd backend
# Ensure MySQL is running (start just mysql with docker-compose)
docker-compose up -d mysql

# Run with dev profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

**Frontend:**
```bash
cd frontend
npm install
npm run dev
# → http://localhost:5173
```

---

## Project Structure

```
Aptio/
├── backend/                    # Spring Boot 3 / Java 21
│   ├── src/main/java/com/slotsync/backend/
│   │   ├── config/             # All Spring configurations
│   │   ├── controller/         # REST API controllers
│   │   ├── service/            # Business logic interfaces + impls
│   │   ├── repository/         # JPA repositories
│   │   ├── domain/             # JPA entities
│   │   ├── dto/                # Request & Response DTOs
│   │   ├── mapper/             # MapStruct mappers
│   │   ├── event/              # Spring domain events
│   │   ├── exception/          # Global exception handling
│   │   ├── security/           # JWT filter + UserDetails
│   │   ├── scheduler/          # @Scheduled jobs
│   │   ├── websocket/          # STOMP broadcast service
│   │   └── specification/      # JPA Specifications
│   └── src/test/               # Unit + Integration tests
│
├── frontend/                   # React 18 + TypeScript + Vite
│   └── src/
│       ├── api/                # Axios + TanStack Query hooks
│       ├── components/         # Reusable UI components
│       ├── pages/              # Route-level page components
│       ├── hooks/              # Custom React hooks
│       ├── store/              # Auth context
│       ├── types/              # TypeScript interfaces
│       └── utils/              # Helper functions
│
├── docker/                     # Docker helper files (MySQL init scripts)
├── nginx/                      # Nginx reverse proxy config
├── docs/                       # Architecture, ER diagram, sequence diagrams
└── .github/workflows/          # GitHub Actions CI/CD
```

---

## API Endpoints

### Authentication
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/v1/auth/register` | Register new user | Public |
| POST | `/api/v1/auth/login` | Login, get tokens | Public |
| POST | `/api/v1/auth/refresh` | Rotate refresh token | Public |
| POST | `/api/v1/auth/logout` | Revoke refresh token | Bearer |

### Providers
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/providers` | Search + filter providers | Public |
| GET | `/api/v1/providers/{id}` | Get provider details | Public |
| POST | `/api/v1/providers` | Create provider profile | PROVIDER |
| PUT | `/api/v1/providers/{id}` | Update provider | PROVIDER |

### Slots
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/slots` | Get available slots by provider | Public |
| POST | `/api/v1/slots` | Create slot | PROVIDER |
| POST | `/api/v1/slots/recurring` | Generate recurring slots | PROVIDER |
| PUT | `/api/v1/slots/{id}` | Update slot | PROVIDER |
| DELETE | `/api/v1/slots/{id}` | Cancel slot | PROVIDER |

### Bookings
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/v1/bookings` | Create booking | CUSTOMER |
| GET | `/api/v1/bookings` | Get my bookings | CUSTOMER |
| DELETE | `/api/v1/bookings/{id}` | Cancel booking | CUSTOMER |
| GET | `/api/v1/bookings/provider` | Get provider's bookings | PROVIDER |

### Waitlist
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/v1/waitlist` | Join waitlist | CUSTOMER |
| DELETE | `/api/v1/waitlist/{id}` | Leave waitlist | CUSTOMER |

### Admin
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/admin/users` | List all users | ADMIN |
| PATCH | `/api/v1/admin/users/{id}/suspend` | Suspend user | ADMIN |
| GET | `/api/v1/admin/bookings` | View all bookings | ADMIN |
| GET | `/api/v1/admin/audit-logs` | View audit logs | ADMIN |
| GET | `/api/v1/admin/reports` | System reports | ADMIN |

---

## Testing

```bash
cd backend

# Run all tests (uses H2 in-memory — no MySQL required)
./mvnw test

# Run tests with JaCoCo coverage report
./mvnw test jacoco:report

# View coverage report
open target/site/jacoco/index.html

# Frontend tests
cd frontend
npm run test
npm run test:coverage
```

---

## CI/CD Pipeline

The GitHub Actions workflow (`.github/workflows/ci.yml`) runs on every push to `main`/`develop`:

1. **Backend Tests** — Maven + JUnit5 + JaCoCo (85% coverage gate)
2. **Frontend Tests** — TypeScript + ESLint + Vitest
3. **Docker Build** — Build and push images to GHCR (main only)
4. **Security Scan** — Trivy vulnerability scan on both images

---

## Documentation

| Document | Location |
|---------|---------|
| Architecture Overview | [docs/architecture.md](docs/architecture.md) |
| ER Diagram | [docs/er-diagram.md](docs/er-diagram.md) |
| Sequence Diagrams | [docs/sequence-diagrams.md](docs/sequence-diagrams.md) |
| Swagger UI | http://localhost/swagger-ui.html |
| OpenAPI JSON | http://localhost/api-docs |

---

## Design Decisions

### Why Pessimistic Locking for Bookings?
In the booking transaction, we issue `SELECT ... FOR UPDATE` on the Slot row. This guarantees that only one transaction can modify the slot at a time, making double-booking **physically impossible** at the database level. The trade-off is slightly higher latency for concurrent booking attempts, which is acceptable given the strong consistency guarantee required.

### Why Optimistic Locking by Default?
For most reads and non-critical updates, `@Version` provides optimistic concurrency control without holding DB locks. This maximizes read throughput. Pessimistic locking is only activated for the booking path.

### Why Spring Events + @Async for Notifications?
The booking transaction commits and returns to the customer immediately. Notification delivery (email, WebSocket, DB) happens asynchronously in a separate thread pool. This ensures a slow mail server cannot block or fail the booking itself.

### Why FIFO Waitlist?
First-In-First-Out is the fairest promotion strategy. The `position` column ensures deterministic ordering and is assigned atomically within the booking transaction.

---

## License

MIT License — See [LICENSE](LICENSE) for details.
