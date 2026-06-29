# CineHub — Monorepo

Welcome to **CineHub**, a modern movie streaming platform. This repository is organized as a monorepo containing the backend API and two frontend applications.

🌐 **Live Demo / Deploy Link**: [https://cinehub.software/auth](https://cinehub.software/auth)

---

## Technologies & Tech Stack

CineHub leverages a modern and robust tech stack across its components:

### 🖥️ Backend REST API (`CineHub/`)
* **Core Framework**: Java 17 + Spring Boot 4.x
* **Security & Authentication**: Spring Security, JWT (JSON Web Tokens), Google OAuth Token Verification
* **Database & Caching**: PostgreSQL (Hosted on **Supabase**), Spring Data JPA, Redis Cache (for fast session caching)
* **Integrations**: Spring Mail (Gmail SMTP for OTP & resets), VNPay (Payment Gateway integration)
* **DevOps & Config**: Docker, Docker Hub, VPS Deployment, Docker Compose
* **Monitoring**: Prometheus (metrics collection via Spring Actuator), Grafana (visualization dashboards)

### 🎬 User Frontend (`user/`)
* **Core**: React 18 + Vite 6 + TypeScript
* **Styling**: Tailwind CSS v4 (using the `@tailwindcss/vite` plugin)
* **Components**: Radix UI Primitives, Lucide Icons, Motion (smooth animations), Sonner (popups & notifications)
* **Streaming**: HLS.js (for HTTP Live Streaming video player playback)
* **State & Networking**: Axios, React Router v7, Google OAuth integration
* **Analytics & Testing**: Recharts, Cypress (E2E testing)

### 📊 Admin Frontend (`admin/`)
* **Core**: React 18 + Vite 6 + Tailwind CSS v4
* **UI Framework**: Material UI (MUI) & `@emotion/react`
* **Components & Interactivity**: React DnD (Drag & Drop support), SheetJS (`xlsx` for exporting reports to Excel)
* **Analytics**: Recharts (admin statistical charts)

---

## Repository Structure

The project is split into three main directories:

1. **[`CineHub/`](file:///home/kien/Code/CineHub/CineHub)**: Backend REST API built with Spring Boot, Spring Security, JWT, VNPay, and PostgreSQL.
2. **[`user/`](file:///home/kien/Code/CineHub/user)**: Frontend application for general users (customers) to browse and stream movies.
3. **[`admin/`](file:///home/kien/Code/CineHub/admin)**: Frontend administration panel for managing movies, genres, users, and transactions.

---

## 1. Backend Setup (`CineHub/`)

The backend is built with **Java 17** and **Spring Boot 3.x**.

### Prerequisites
- Java 17+ (JDK)
- PostgreSQL database
- Maven (or use the provided `./mvnw` wrapper)

### Setup & Run
1. Navigate to the backend directory:
   ```bash
   cd CineHub
   ```
2. Create and configure your `.env` file (see the [Backend README](file:///home/kien/Code/CineHub/CineHub/README.md) for required keys).
3. Build and run:
   ```bash
   ./mvnw spring-boot:run
   ```

---

## 2. Frontend User Application (`user/`)

A React + TypeScript SPA for users to browse, search, and watch movies.

### Prerequisites
- Node.js (v18+)
- npm or yarn

### Setup & Run
1. Navigate to the user directory:
   ```bash
   cd user
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Run the development server:
   ```bash
   npm run dev
   ```

---

## 3. Frontend Admin Dashboard (`admin/`)

An administrative interface for managing the platform's resources.

### Prerequisites
- Node.js (v18+)
- npm or yarn

### Setup & Run
1. Navigate to the admin directory:
   ```bash
   cd admin
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Run the development server:
   ```bash
   npm run dev
   ```

---

## 4. Monitoring Configuration (Prometheus & Grafana)

The project includes pre-configured monitoring services to check API performance, JVM memory, CPU usage, and system health.

### Setup Locally
1. Spin up the containers using Docker Compose:
   ```bash
   docker compose up -d
   ```
2. Check Prometheus target status at [http://localhost:9090/targets](http://localhost:9090/targets).
3. Open Grafana at [http://localhost:3002](http://localhost:3002) (Default Login: `admin` / `admin`).
   - Add **Prometheus** as a data source with URL `http://prometheus:9090`.
   - Import JVM Dashboard (ID `4701` or `11378`).

### Production Deployment via Nginx Proxy Manager
To host Grafana with HTTPS securely on your VPS:
1. Point a DNS **A Record** (e.g., `grafana.cinehub.software`) to your VPS IP address.
2. Open Nginx Proxy Manager admin page (port `81`) and **Add Proxy Host**:
   - **Domain Names**: `grafana.cinehub.software`
   - **Scheme**: `http`
   - **Forward Hostname / IP**: `grafana` (resolves internally via Docker network)
   - **Forward Port**: `3000`
   - Enable **Websockets Support** and **Block Common Exploits**.
3. Under the **SSL** tab:
   - Select **Request a new SSL Certificate** (Let's Encrypt).
   - Enable **Force SSL**.
4. Access Grafana securely at `https://grafana.cinehub.software`.

---

For more details on each sub-project, please refer to their respective directory READMEs:
- [Backend REST API README](file:///home/kien/Code/CineHub/CineHub/README.md)
- [User Frontend README](file:///home/kien/Code/CineHub/user/README.md)
- [Admin Frontend README](file:///home/kien/Code/CineHub/admin/README.md)
