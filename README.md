# Fashion E-Commerce Platform

A full-stack fashion e-commerce application for browsing clothing, managing product variants, and placing orders. It includes role-based administration and secure authentication through email/password or Google OAuth2.

## Key Features

- **Clothing catalog:** Browse products, details, images, and size options.
- **Product variants:** Manage variants and clothing sizes.
- **Shopping cart and orders:** Build a cart and submit orders.
- **Authentication:** Account registration, email/password login, Google OAuth2 integration, and JWT-based sessions.
- **Role-based access:** Customer and administrator workflows.
- **Admin dashboard:** Manage products and variants and view sales-related information.

## Tech Stack

| Layer | Technologies |
| --- | --- |
| Frontend | React 19, Vite, React Router, React-Bootstrap, Axios, Recharts |
| Backend | Java 17, Spring Boot 3.5.6, Spring Security, Spring Data JPA |
| Authentication | JWT, Google OAuth2 |
| Database | PostgreSQL |
| Build tools | Maven, npm |

## Project Structure

```text
e-boutique/
├── ecommerce_back/      # Spring Boot REST API, security, persistence
│   └── src/main/java/com/ecommerce/demo/
│       ├── auth/
│       ├── config/
│       ├── controller/
│       ├── models/
│       ├── repositories/
│       └── service/
└── ecommerce_front/     # React frontend
    └── src/components/
        ├── HomePage/
        ├── Products/
        ├── ProductsPage/
        ├── admin/
        ├── auth/
        └── cart/
```

## Getting Started

### Prerequisites

- Java 17
- Node.js and npm
- PostgreSQL
- Google OAuth2 credentials if using Google sign-in

### Clone the repository

```bash
git clone https://github.com/ikraammel/e-boutique.git
cd e-boutique
```

### Backend

Configure PostgreSQL, JWT signing credentials, and Google OAuth2 client settings locally before starting the Spring Boot API. Keep all credentials out of version control.

```bash
cd ecommerce_back
./mvnw spring-boot:run
```

On Windows, use `mvnw.cmd spring-boot:run`.

### Frontend

From the repository root, in a separate terminal:

```bash
cd ecommerce_front
npm install
npm run dev
```

Configure the frontend API URL to match the running backend.

## About

This project demonstrates full-stack web development, e-commerce domain modeling, role-based access control, and integration of JWT and OAuth2 authentication.
