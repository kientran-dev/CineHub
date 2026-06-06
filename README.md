# CineHub

A full-stack movie streaming platform — monorepo containing **backend API**, **user-facing app**, and **admin dashboard**.

---

## Repository Structure

```text
CineHub/
├── CineHub/       # Spring Boot backend (REST API)
├── user/          # User-facing movie streaming app (React + Vite)
└── admin/         # Admin dashboard (React + Vite)
```

---

## Tech Stack

### Backend — `CineHub/`
- **Java** + **Spring Boot**
- Spring Security + JWT authentication
- Google OAuth2.0
- VNPay payment integration
- Maven (`mvnw`)

### User App — `user/`
- **React** + **Vite** + **TypeScript**
- React Router, Radix UI, Tailwind CSS
- HLS.js (video streaming), Embla Carousel
- Google OAuth (`@react-oauth/google`)
- Axios

### Admin Dashboard — `admin/`
- **React** + **Vite** + **TypeScript**
- React Router, Radix UI, Tailwind CSS
- MUI (Material UI), Recharts
- React Hook Form, XLSX export
- Drag & Drop (`react-dnd`)

---

## Getting Started

### Prerequisites
- **Java 17+** & **Maven** (for backend)
- **Node.js** (LTS) & **npm** (for frontend apps)

---

### 1) Run the Backend

```bash
cd CineHub
cp .env.example .env   # configure your environment variables
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

---

### 2) Run the User App

```bash
cd user
npm install
npm run dev
```

Runs at `http://localhost:5173` by default.

---

### 3) Run the Admin Dashboard

```bash
cd admin
npm install
npm run dev
```

Runs at `http://localhost:5174` by default.

---

## Environment Variables

Each sub-project has its own `.env` file. Refer to the `.env` in each folder for required variables:

| Project | File | Key variables |
|---------|------|---------------|
| Backend | `CineHub/.env` | DB credentials, JWT secret, VNPay keys, OAuth2 config |
| User FE | `user/.env` | API base URL, Google Client ID |

---

## Development Notes

- Run each app in its **own terminal** — they are independent.
- Frontend apps communicate with the backend via REST API — make sure the backend is running first.
- API collection for testing: `CineHub/ImportAPIPostman.json` (import into Postman).

---

## License

[MIT](CineHub/LICENSE)
