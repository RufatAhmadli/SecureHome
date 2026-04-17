# SecureHome

A local-first smart home management system. All device control, state, and activity data is processed and stored on the local hub — no mandatory cloud dependency.

## What it does

- Manage multiple homes with rooms and IoT devices
- Role-based access control: Owner, Admin, Member, Guest
- Real-time device state sync via WebSocket
- MQTT-based device communication (Smart Locks, Cameras)
- Full activity and audit logging
- Works on your local network during internet outages

## Tech Stack

| Layer      | Technology                   |
| ---------- | ---------------------------- |
| Backend    | Java 21, Spring Boot 4.0.2   |
| Frontend   | React 18, Vite 6             |
| Database   | PostgreSQL 15                |
| Migrations | Liquibase                    |
| Auth       | JWT (JJWT 0.12.3)            |
| Messaging  | MQTT (Eclipse Mosquitto 2.0) |
| Real-time  | Spring WebSocket (STOMP)     |
| Mapping    | MapStruct 1.6.3              |
| Build      | Gradle                       |

## Prerequisites

- Java 21
- Node.js 18+
- Docker & Docker Compose

## Getting Started

**1. Clone the repository**

```bash
git clone https://github.com/RufatAhmadli/SecureHome.git
cd SecureHome
```

**2. Create a `.env` file in the project root**

```env
JDBC_DATABASE=secureHomeDb
JDBC_URL=jdbc:postgresql://localhost:5433/secureHomeDb
JDBC_USERNAME=postgres
JDBC_PASSWORD=your_password
SECRET_KEY=your_jwt_secret_base64
EXPIRATION_TIME=3600000
```

**3. Start infrastructure (PostgreSQL + MQTT broker)**

```bash
docker-compose up -d
```

**4. Run the backend**

```bash
./gradlew bootRun
```

**5. Build and run the frontend**

```bash
cd frontend
npm install
npm run dev
```

The application is available at `http://localhost:8080`.  
Swagger UI is available at `http://localhost:8080/swagger-ui/index.html`.

## API Overview

### Authentication — `/api/v1/auth`

| Method | Path           | Description                 |
| ------ | -------------- | --------------------------- |
| POST   | `/register`    | Register a new account      |
| POST   | `/login`       | Login and receive JWT token |
| GET    | `/me`          | Get current user            |
| PATCH  | `/me`          | Update name                 |
| PATCH  | `/me/password` | Change password             |
| DELETE | `/me`          | Delete account              |

### Homes — `/api/v1/homes`

| Method | Path           | Description      |
| ------ | -------------- | ---------------- |
| GET    | `/`            | List your homes  |
| POST   | `/`            | Create a home    |
| PUT    | `/{id}`        | Update home      |
| DELETE | `/{id}`        | Delete home      |
| GET    | `/memberships` | Your memberships |

### Members — `/api/v1/homes/{homeId}/members`

| Method | Path          | Description         |
| ------ | ------------- | ------------------- |
| GET    | `/`           | List members        |
| POST   | `/addMember`  | Add member by email |
| PUT    | `/{memberId}` | Update member role  |
| DELETE | `/{memberId}` | Remove member       |

### Rooms — `/api/v1/rooms`

| Method | Path    | Description |
| ------ | ------- | ----------- |
| POST   | `/`     | Create room |
| PUT    | `/{id}` | Update room |
| DELETE | `/{id}` | Delete room |

### Smart Locks — `/api/v1/smart-locks`

| Method | Path             | Description        |
| ------ | ---------------- | ------------------ |
| GET    | `/home/{homeId}` | List locks in home |
| POST   | `/`              | Register a lock    |
| PATCH  | `/{id}/lock`     | Lock device        |
| PATCH  | `/{id}/unlock`   | Unlock device      |
| DELETE | `/{id}`          | Remove device      |

### Cameras — `/api/v1/cameras`

| Method | Path             | Description          |
| ------ | ---------------- | -------------------- |
| GET    | `/home/{homeId}` | List cameras in home |
| POST   | `/`              | Register a camera    |
| PATCH  | `/{id}/arm`      | Arm camera           |
| PATCH  | `/{id}/disarm`   | Disarm camera        |
| DELETE | `/{id}`          | Remove device        |

### Activity Logs — `/api/v1/homes/{homeId}/activity-logs`

| Method | Path | Description            |
| ------ | ---- | ---------------------- |
| GET    | `/`  | Get home activity logs |

## Roles and Permissions

| Action               | Owner | Admin | Member | Guest |
| -------------------- | :---: | :---: | :----: | :---: |
| Create / delete home |   ✓   |       |        |       |
| Manage rooms         |   ✓   |   ✓   |        |       |
| Onboard devices      |   ✓   |   ✓   |        |       |
| Control devices      |   ✓   |   ✓   |   ✓    |       |
| View dashboard       |   ✓   |   ✓   |   ✓    |   ✓   |
| Manage members       |   ✓   |       |        |       |

## MQTT Integration

Devices publish events to the broker using this topic format:

```
home/{homeId}/{deviceType}/{deviceId}/{event}
```

Example: `home/1/lock/2/status`

The backend subscribes to `home/#`, normalizes incoming messages into `DeviceCommand` objects, updates the database, and broadcasts a refresh signal to the dashboard via WebSocket on `/topic/home/{homeId}`.

Supported event types: `status`, `telemetry`, `heartbeat`  
Supported protocols: `MQTT`, `MATTER` (planned), `HTTP` (planned)

## WebSocket

Connect to `/ws` and subscribe to `/topic/home/{homeId}` to receive real-time refresh signals when device state changes.

## Running Tests

```bash
./gradlew test
```

Integration tests use Testcontainers — Docker must be running.

## Project Structure

```
src/main/java/example/web/securehome/
├── config/          # Security, JWT filter, WebSocket
├── controller/      # REST controllers
├── service/         # Business logic
├── entity/          # JPA entities
├── dto/             # Request and response DTOs
├── repository/      # Data access
├── protocol/        # MQTT subscriber, adapter, router
├── event/           # Domain events and listeners
├── enums/           # Role, status, protocol enums
├── exception/       # Custom exceptions and global handler
├── mapper/          # MapStruct mappers
└── util/            # Security utilities

frontend/
├── src/pages/       # Login, Register, Dashboard, Profile
├── src/components/  # Rooms, Devices, Members, ActivityLog tabs
├── src/api/         # Axios API client modules
└── src/hooks/       # WebSocket hook
```
