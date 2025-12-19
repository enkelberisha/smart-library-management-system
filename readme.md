# 📚 Smart Library Management System
[![Java](https://img.shields.io/badge/Java-17+-ED8B00.svg?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F.svg?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-336791.svg?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-Server--Side-005F0F.svg?logo=thymeleaf&logoColor=white)](https://www.thymeleaf.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED.svg?logo=docker&logoColor=white)](https://www.docker.com/)
[![AI](https://img.shields.io/badge/AI-OpenRouter%20%7C%20OpenAI-8A2BE2.svg)](https://openrouter.ai/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A modern **Library Management System** built with **Spring Boot**, **PostgreSQL**, **Thymeleaf**, and **Docker**, featuring **role-based access control**, **book tracking**, and an **AI-powered recommendation system**.

This project was developed as an academic and practical showcase of:
- backend architecture
- MVC design patterns
- database-driven applications
- session-based authentication
- and lightweight AI integration using OpenRouter (OpenAI-compatible API)

---

## ✨ Features Overview

### 👤 User Features
- User authentication (login & registration)
- Add, edit, delete personal books
- Track reading status:
    - `reading`
    - `completed`
    - `want_to_read`
- Filter books by genre
- View personal library dashboard
- Generate **AI-powered book recommendations**

### 🛠 Admin Features
- Admin-only dashboard
- View all users (excluding admins)
- View books per user
- Filter all books by genre
- Edit or delete users
- Edit or delete any book in the system

### 🤖 AI Features 
- AI-generated book suggestions based on:
    - user reading history
    - preferred genres
    - favorite authors
- Uses **OpenRouter API** (OpenAI-compatible)


---



## 🧠 AI for Administrators (Natural Language Queries)

In addition to user-facing recommendations, the system includes an **AI-powered admin assistant** that allows administrators to query the database using **natural language**.

This feature enables admins to ask questions such as:
- “Show all users who completed fantasy books”
- “List books that are currently being read”
- “Find users with the most books”
- “Show all books grouped by genre”

### How it works:
1. The admin enters a natural language query
2. The query is sent to the AI engine
3. The AI translates the request into a valid **SQL query**
4. The backend **executes the SQL safely**
5. Results are displayed in the admin dashboard

### Safety & Constraints:
- Only predefined tables and columns are allowed
- SQL output is strictly validated
- Role-based access ensures only admins can use this feature
- Prevents destructive queries (INSERT, UPDATE, DELETE)

This provides a powerful yet controlled way for administrators to explore system data without writing SQL manually.

## 🐳 Run with Docker

### Prerequisites
- Docker
- Docker Compose

---

### 1️⃣ Build and start the application
From the project root directory:

```bash
docker compose up --build
