# 🍖 Mesara App - Butchery Stock Control System

A streamlined, secure, and self-hosted inventory management application designed specifically for butchery shops. It provides strict control over daily meat weights, stock movements, and daily cash register revenues.

## 🚀 Key Features

* **Precise Stock Control:** Track exact movements of items in kilograms. The system explicitly records **Purchase (Nabavka)**, **Sale (Prodato)**, and **Waste/Scrap (Otpis)**.
* **Daily Revenue Tracking:** Shop workers can input the daily cash register totals (Pazar) alongside their stock numbers.
* **Advanced PDF Reporting:** Generate detailed "Business Analytics Reports" (Izveštaj o Analitici Poslovanja).
   * Filter by specific time periods, one or multiple shops, and selected articles.
   * Dynamically generated PDF reports featuring visual data (Pie charts for Share of Purchases/Sales/Waste, Bar charts for store comparisons) and detailed balance tables.
* **🔒 Secure Access:** Role-based access using **Spring Security** with BCrypt password hashing to ensure only authorized workers and admins can view or modify data.
* **Self-Hosted & Tunneling:** Fully containerized using **Docker Compose** for easy local deployment, and securely exposed to the public internet via **Cloudflare Tunnels**.

## 🛠 Tech Stack

* **Backend:** Java 21, Spring Boot 3.4, Spring Security
* **Database:** MySQL 8
* **ORM:** Spring Data JPA (Hibernate)
* **Frontend:** Thymeleaf, Bootstrap 4
* **Reporting:** PDF Generation with embedded dynamic charts
* **DevOps & Infrastructure:** Docker Desktop, Docker Compose, Cloudflare Tunnel
* **Security:** Environment Variables (`.env`) for secret management, BCrypt

## 🏗 Architecture & Strategy

* **Hybrid Language Strategy:** The backend logic, database schema, and documentation are written in **English** to maintain professional coding standards. The frontend user interface and generated PDF reports are in **Serbian**, tailored for local shop workers and management.
* **Containerized Deployment:** The entire system (Spring Boot app + MySQL database) is orchestrated using a `docker-compose.yml` file, ensuring a consistent environment and easy startup.
* **Secure Remote Access:** Instead of traditional port forwarding, the application uses a Cloudflare Tunnel service to securely expose the local server to a public HTTPS URL without exposing the local network.

## 💻 How to Run Locally

To run this project on your local machine, follow these steps:

### Prerequisites
* **Docker** and **Docker Compose** (Docker Desktop) installed
* Git

### Steps

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/DanilG93/mesara-erp.git](https://github.com/DanilG93/mesara-erp.git)
   cd mesara-erp

2. **Configure Environment Variables:**

* For security reasons, database credentials are externalized. Create a .env file in the root directory (this file is ignored by git via .gitignore):
  ```
   DB_HOST=yuor_name_hots
   DB_PORT=yuor_port
   DB_NAME=yuor_db
   DB_USER=yuor_user
   DB_PASS=your_secure_password
   ADMIN_USER=yuor_username_for_login
   ADMIN_PASS=your_admin_password
  ```
  
3. **Build and Run with Docker Compose:**
*  Run the following command to build the Spring Boot application image and start both the app and the database containers:
   ```
   docker-compose up -d --build
   ```
   
4. **Access the Application:**

* Locally: Open your browser and navigate to http://localhost:8080.
* Remote: Access via your configured Cloudflare Tunnel public URL.
