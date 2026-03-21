# Mesara ERP - Butchery Stock Control System 🥩

A streamlined, secure, and self-hosted inventory management and reconciliation system designed specifically for retail butchery shops. Built as a **Hybrid Monolith**, it combines server-side HTML rendering with an independent, "Stateless" REST API.

Unlike traditional Point-of-Sale (POS) systems that track every single receipt, this application focuses on strict **daily stock justification**, data integrity, and overall cash register revenues.

---

<img width="1875" height="925" alt="login-mesara-ERP" src="https://github.com/user-attachments/assets/82fe6983-6265-46e1-8a2a-00a9d7a0b36d" />
<img width="1875" height="925" alt="daily-entry-Mesara-ERP" src="https://github.com/user-attachments/assets/72e93a87-233f-48b3-8a75-ae3a5a885815" />

![Mesara ERP _ BI Analitika](https://github.com/user-attachments/assets/0a2edb5f-db0e-4679-ac59-9047598ad8e4)



## 🚀 Key Features

### 1. Strict Daily Reconciliation (The Core Engine)
At the end of the day, shop workers input the remaining physical stock and the total cash revenue. For each article, the system tracks 4 key metrics:
* **Purchase (Nabavka):** Goods arrived from the central warehouse.
* **Sale (Prodato):** Goods sold to customers.
* **Return (Povrat):** Goods returned to the central warehouse.
* **Waste (Otpis):** Spoiled or discarded goods.

The system enforces the strict formula: `Difference = Purchase - (Sale + Return + Waste)`. The ultimate goal is for the difference to be `0`, proving every gram of meat is justified.

### 2. Security & Authentication (Stateless JWT)
* Completely removed HTTP sessions (Strict `STATELESS` policy) using Spring Security.
* **Hybrid Verification:** A custom `JwtAuthenticationFilter` intercepts and validates both `Authorization: Bearer` headers (for API calls) and HTTP Cookies (`jwt_token`) for secure UI page loads.
* Custom Frontend Login/Logout flow using Vanilla JS with automatic redirects for unauthenticated users.

### 3. Ledger Architecture for Data Integrity
The database separates the current state from historical logs:
* **ProductStock:** Tracks the exact current physical amount of items in real-time.
* **StockMovement:** Acts as an immutable ledger, recording the exact history of every action for auditing purposes.

### 4. Advanced PDF Reporting & Analytics
* A powerful dashboard for generating "Business Analytics Reports" filtered by time periods, stores, and specific articles.
* Frontend dynamically renders visual data (Pie & Bar charts).
* Charts are converted to **Base64** images and sent to the backend, where the `openhtmltopdf` library and Thymeleaf templates generate professional, print-ready PDF documents with embedded fonts.

### 5. Modern REST API & Web UI
* Independent `@RestController` endpoints for Auth, Analytics, Inventory, and Daily Reports.
* Beautiful, fully responsive interface based on the **AdminLTE** template (Bootstrap 4).
* Secure navigation where Sidebar fragments are dynamically protected based on JWT cookies.

---

## 🏗 Architecture & Strategy

* **Hybrid Language Strategy:** The backend logic, database schema, REST APIs, and documentation are written in **English** to maintain professional coding standards. The frontend user interface and generated PDF reports are in **Serbian**, tailored for local shop workers and management.
* **Layered Architecture:** Strict separation of concerns (`controller.api` for JSON data vs `controller.web` for Thymeleaf views).
* **Containerized Deployment:** The entire system (Spring Boot app + MySQL database) is orchestrated using Docker, ensuring a consistent environment, easy startup, and secure isolated networking.

---

## 🛠 Tech Stack

* **Backend:** Java 21, Spring Boot 3.4
* **Security:** Spring Security 6, JWT (JSON Web Tokens)
* **Database & ORM:** MySQL 8, Spring Data JPA (Hibernate)
* **Frontend:** Thymeleaf, Bootstrap 4 (AdminLTE 3), Vanilla JavaScript (Fetch API)
* **Reporting:** PDF Generation (`openhtmltopdf`) with Base64 charts
* **Documentation:** Swagger / OpenAPI 3.0
* **DevOps:** Docker Desktop, Docker Compose

---

## 💻 How to Run Locally

### Prerequisites
* **Docker** and **Docker Compose** (Docker Desktop) installed
* Git

### Steps

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/DanilG93/mesara-erp.git](https://github.com/DanilG93/mesara-erp.git)
   cd mesara-erp
   ```
2. **Configure Environment Variables:**
* For security reasons, database credentials are externalized. Create a .env file in the root directory (this file is ignored by git via .gitignore):
   ```bash
   DB_HOST=your_db_host
   DB_PORT=3306
   DB_NAME=your_db_name
   DB_USER=your_db_user
   DB_PASS=your_secure_password
   ADMIN_USER=your_username_for_login
   ADMIN_PASS=your_admin_password
   ```
3. **Build and Run with Docker Compose:**
*Run the following command to build the Spring Boot application image and start both the app and the database containers:
   ```bash
   docker-compose up -d --build
   ```
3. **Access the Application:**
* **Web UI:** Open your browser and navigate to `http://localhost:8080` (or 8081 depending on your setup).
* **API Documentation:** Access the Swagger UI at `http://localhost:8080/swagger-ui/index.html`.

👨‍💻 **Author:**
* **Danil Gomanjuk**
