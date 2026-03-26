# GymCoach — Backend

A RESTful API built with Spring Boot for a fitness platform that connects users with certified personal trainers and provides AI-generated workout plans.

The frontend application is available here:
https://github.com/KeviN-XVII/GymCoachFE

---

## Tech Stack

* **Java 21** + **Spring Boot 3**
* **Spring Security** — JWT-based stateless authentication
* **PostgreSQL** — relational database
* **JPA / Hibernate** — ORM
* **Stripe** — payment processing
* **Cloudinary** — avatar image upload and hosting
* **OpenRouter API** — AI workout plan generation (Step-3.5 Flash model)
* **Swagger / OpenAPI** — API documentation

---

## Features

* User registration and login with role-based access (USER, TRAINER)
* JWT authentication with custom filter chain
* AI-powered workout plan generation based on user's physical profile
* Stripe checkout integration for purchasing trainer plans
* Stripe webhook handling to confirm payments
* Workout plan management (plans, days, exercises)
* Trainer profile management with pricing
* File upload for avatars via Cloudinary
* Full input validation with custom error messages

---

## Project Structure

```
src/main/java/com/gymcoach/gymcoach/
├── controllers/       # REST endpoints
├── services/          # Business logic
├── repositories/      # JPA repositories
├── entities/          # JPA entities
├── dto/               # Request and response DTOs
├── security/          # JWT filter, JWT tools, SecurityConfig
├── config/            # Stripe, Cloudinary config
└── exceptions/        # Custom exceptions and global handler
```

---

## Prerequisites

* Java 21
* Maven
* PostgreSQL
* Stripe account (test mode)
* Cloudinary account
* OpenRouter API key

---

## Setup

### 1. Create the database

```sql
CREATE DATABASE gymcoach;
```

### 2. Configure environment variables

Create an `.env` file:

```env
DB_URL=jdbc:postgresql://localhost:5432/gymcoach
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password

JWT_SECRET=your_jwt_secret_at_least_32_characters

CLOUDINARY_NAME=your_cloudinary_name
CLOUDINARY_API_KEY=your_cloudinary_api_key
CLOUDINARY_API_SECRET=your_cloudinary_api_secret

STRIPE_PUBLIC_KEY=pk_test_xxx
STRIPE_SECRET_KEY=sk_test_xxx
STRIPE_WEBHOOK_SECRET=whsec_xxx

OPENROUTER_API_KEY=your_openrouter_key
```

### 3. Run the application

```bash
mvn spring-boot:run
```

Server runs on:
http://localhost:5174

---

## Stripe Webhook (Local Testing)

```bash
stripe listen --forward-to localhost:5174/purchases/webhook
```

Copy the `whsec_xxx` secret into your environment variables.

---

## API Endpoints

### Auth (public)

| Method | Endpoint                 | Description               |
| ------ | ------------------------ | ------------------------- |
| POST   | `/auth/login`            | Login — returns JWT token |
| POST   | `/auth/register/user`    | Register as USER          |
| POST   | `/auth/register/trainer` | Register as TRAINER       |

### Users

| Method | Endpoint            | Role | Description             |
| ------ | ------------------- | ---- | ----------------------- |
| GET    | `/users/me`         | Any  | Get current user data   |
| PUT    | `/users/me`         | Any  | Update user data        |
| PATCH  | `/users/me/avatar`  | Any  | Upload avatar           |
| GET    | `/users/me/profile` | USER | Get physical profile    |
| PUT    | `/users/me/profile` | USER | Update physical profile |

### Trainers

| Method | Endpoint               | Role    | Description                   |
| ------ | ---------------------- | ------- | ----------------------------- |
| GET    | `/trainers`            | Any     | List all trainers (paginated) |
| GET    | `/trainers/{id}`       | Any     | Get trainer by ID             |
| GET    | `/trainers/me/profile` | TRAINER | Get own trainer profile       |
| PUT    | `/trainers/me/profile` | TRAINER | Update trainer profile        |

### Workout Plans

| Method | Endpoint                       | Role    | Description                 |
| ------ | ------------------------------ | ------- | --------------------------- |
| GET    | `/workout-plans/me`            | USER    | Get user's plans            |
| GET    | `/workout-plans/me/{id}`       | USER    | Get plan by ID              |
| DELETE | `/workout-plans/me/{id}`       | USER    | Delete plan                 |
| GET    | `/workout-plans/trainer/me`    | TRAINER | Get trainer's created plans |
| POST   | `/workout-plans/user/{userId}` | TRAINER | Create plan for user        |

### Workout Days

| Method | Endpoint                      | Role         | Description        |
| ------ | ----------------------------- | ------------ | ------------------ |
| GET    | `/workout-days/plan/{planId}` | USER/TRAINER | Get days of a plan |
| POST   | `/workout-days/plan/{planId}` | TRAINER      | Add day to plan    |
| DELETE | `/workout-days/{id}`          | TRAINER      | Delete day         |

### Exercises

| Method | Endpoint                 | Role         | Description            |
| ------ | ------------------------ | ------------ | ---------------------- |
| GET    | `/exercises/day/{dayId}` | USER/TRAINER | Get exercises of a day |
| POST   | `/exercises/day/{dayId}` | TRAINER      | Add exercise to day    |
| PUT    | `/exercises/{id}`        | TRAINER      | Update exercise        |
| DELETE | `/exercises/{id}`        | TRAINER      | Delete exercise        |

### Purchases

| Method | Endpoint                                 | Role    | Description                    |
| ------ | ---------------------------------------- | ------- | ------------------------------ |
| POST   | `/purchases/checkout/{trainerProfileId}` | USER    | Create Stripe checkout session |
| POST   | `/purchases/webhook`                     | Public  | Stripe webhook                 |
| GET    | `/purchases/me`                          | USER    | Get user's purchases           |
| GET    | `/purchases/trainer/me`                  | TRAINER | Get trainer's received orders  |

### AI

| Method | Endpoint       | Role | Description              |
| ------ | -------------- | ---- | ------------------------ |
| POST   | `/ai/generate` | USER | Generate AI workout plan |

---

## Authentication

All protected endpoints require a JWT token:

```
Authorization: Bearer <token>
```

* Obtained via `POST /auth/login`
* Expires after 7 days

---

## Database Schema

* `User` — base entity with role (USER/TRAINER)
* `UserProfile` — physical data
* `TrainerProfile` — trainer info & pricing
* `WorkoutPlan` — linked to user/trainer
* `WorkoutDay` — plan structure
* `Exercise` — workout details
* `Purchase` — Stripe payment tracking

---

## Enums

* `Role`: USER, TRAINER
* `Goal`: MASSA, DIMAGRIMENTO, TONIFICAZIONE
* `Level`: PRINCIPIANTE, INTERMEDIO, AVANZATO
* `Gender`: UOMO, DONNA, PREFERISCO_NON_SPECIFICARLO
* `PurchaseStatus`: PENDING, COMPLETED, FAILED, REFUNDED

---

## Author

Kevin Quarta
GitHub: https://github.com/KeviN-XVII
