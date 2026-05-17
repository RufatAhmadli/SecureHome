# SecureHome — Entity-Relationship Diagram

This document matches the **deployed JPA data model** in the SecureHome codebase. Optional entities at the end reflect the **report / full product** roadmap (not yet implemented as `@Entity` classes).

---

## 1. Implemented schema (current deliverable)

```mermaid
erDiagram
    USER {
        bigint id PK
        string first_name
        string last_name
        string email UK
        string password
        bigint profile_id FK "nullable"
        datetime created_at
        datetime updated_at
        string created_by
        string updated_by
    }

    USER_PROFILE {
        bigint id PK
        string address
        string city
        string timezone
        string phone_number
        date birth_date
        boolean email_notifications
        boolean sms_notifications
        datetime created_at
        datetime updated_at
        string created_by
        string updated_by
    }

    ROLE {
        bigint id PK
        string role_name UK
    }

    USERS_ROLES {
        bigint user_id FK
        bigint roles_id FK
    }

    HOME {
        bigint id PK
        string name
        string address
        string city
        string description
        string timezone
        datetime created_at
        datetime updated_at
        string created_by
        string updated_by
    }

    HOME_MEMBER {
        bigint id PK
        string role "OWNER|ADMIN|MEMBER|GUEST"
        bigint user_id FK
        bigint home_id FK
        datetime created_at
        datetime updated_at
        string created_by
        string updated_by
    }

    ROOM {
        bigint id PK
        string room_name
        int floor
        string description
        bigint home_id FK
        datetime created_at
        datetime updated_at
        string created_by
        string updated_by
    }

    DEVICE {
        bigint id PK
        string device_name
        string display_name
        string protocol
        string status
        bigint room_id FK "nullable"
        bigint home_id FK
        datetime created_at
        datetime updated_at
        string created_by
        string updated_by
    }

    CAMERA {
        bigint id PK "FK -> DEVICE"
        string resolution
        boolean motion_detection
        boolean night_vision
        string storage_location
    }

    SMART_LOCK {
        bigint id PK "FK -> DEVICE"
        string lock_status
        boolean auto_lock
        int auto_lock_delay_seconds
        boolean tamper_alert
    }

    USER ||--o| USER_PROFILE : "1:1"
    USER }o--o{ ROLE : "M:N via users_roles"
    USER ||--o{ HOME_MEMBER : "membership"
    HOME ||--o{ HOME_MEMBER : "members"
    HOME ||--o{ ROOM : "contains"
    HOME ||--o{ DEVICE : "owns"
    ROOM ||--o{ DEVICE : "optional room"
    DEVICE ||--|| CAMERA : "joined subclass"
    DEVICE ||--|| SMART_LOCK : "joined subclass"
```

**Notes**

- **DEVICE** uses JPA `JOINED` inheritance: base row in `devices`; `cameras` and `locks` rows share the same primary key as `devices.id`.
- **HomeMember.role** is per-home membership (Owner/Admin/Member/Guest). **Role** on `USER` is global app role (e.g. `ROLE_USER`, `ROLE_ADMIN`) via `users_roles`.
- Audit columns come from `BaseEntity` (`created_at`, `updated_at`, `created_by`, `updated_by`) on all entities that extend it except **Role** (no audit in code).

---

## 2. Report-aligned extensions (full product — optional)

Add when you implement activity logging, automation, and optional token storage:

```mermaid
erDiagram
    ACTIVITY_LOG {
        bigint id PK
        bigint home_id FK
        bigint user_id FK "nullable"
        bigint device_id FK "nullable"
        string action_type
        string payload_encrypted
        datetime occurred_at
    }

    AUTOMATION_RULE {
        bigint id PK
        bigint home_id FK
        bigint created_by_user_id FK
        string name
        boolean enabled
        int priority
        string trigger_spec
        string action_spec
        datetime created_at
    }

    RULE_DEVICE_MAP {
        bigint rule_id FK
        bigint device_id FK
    }

    HOME ||--o{ ACTIVITY_LOG : "logs"
    USER ||--o{ ACTIVITY_LOG : "actor"
    DEVICE ||--o{ ACTIVITY_LOG : "subject"
    HOME ||--o{ AUTOMATION_RULE : "rules"
    USER ||--o{ AUTOMATION_RULE : "created_by"
    AUTOMATION_RULE }o--o{ DEVICE : "M:N via rule_device_map"
```

---

## 3. How to use in your report

1. Copy section **1** into [Mermaid Live Editor](https://mermaid.live) or a Markdown renderer that supports Mermaid (GitHub, many IDEs).
2. Export as PNG/SVG for Word: **Figure — ER Diagram: SecureHome Local Database Schema**.
3. In the caption, state: *“Solid model reflects the current Spring Data JPA schema; subsection 2 depicts planned tables for activity logging and automation described in the requirements document.”*

---

## 4. Cardinalities (text summary)

| Relationship | Cardinality |
|-------------|-------------|
| USER — USER_PROFILE | 1 : 0..1 |
| USER — ROLE | M : N (`users_roles`) |
| USER — HOME_MEMBER | 1 : N |
| HOME — HOME_MEMBER | 1 : N |
| HOME — ROOM | 1 : N |
| HOME — DEVICE | 1 : N |
| ROOM — DEVICE | 1 : N (nullable `room_id`) |
| DEVICE — CAMERA | 1 : 0..1 (subtype) |
| DEVICE — SMART_LOCK | 1 : 0..1 (subtype) |
