# Mesara App - Butchery Stock Control System

A streamlined, secure, and self-hosted inventory management and reconciliation system designed specifically for retail butchery shops. Unlike traditional Point-of-Sale (POS) systems that track every single receipt, this application focuses on strict **daily stock justification** and overall cash register revenues.

## 🚀 Key Features

* **Strict Daily Reconciliation (The Core Engine):** At the end of the day, shop workers input the remaining physical stock and the total cash revenue. For each article, the system tracks 4 key metrics:
  * **Purchase (Nabavka):** Goods arrived from the central warehouse.
  * **Sale (Prodato):** Goods sold to customers.
  * **Return (Povrat):** Goods returned to the central warehouse.
  * **Waste (Otpis):** Spoiled or discarded goods.
  * *The system enforces the formula:* `Difference = Purchase - (Sale + Return + Waste)`. The ultimate goal is for the difference to be `0`, proving every gram of meat is justified.
* **Ledger Architecture for Data Integrity:** The database separates current state from historical logs:
  * `ProductStock`: Tracks the exact current physical amount of items in real-time.
  * `StockMovement`: Acts as an immutable ledger, recording the exact history of every action (Purchase, Sale, Return, Waste) for auditing.
* **Advanced PDF Reporting & Analytics:** A powerful dashboard for generating "Business Analytics Reports" filtered by time periods, stores, and specific articles. 
  * Frontend dynamically renders visual data (Pie & Bar charts).
  * Charts are converted to *Base64* images and sent to the backend, where the `openhtmltopdf` library and Thymeleaf templates generate professional, print-ready PDF documents with embedded fonts.
* **Secure Access:** Role-based access using Spring Security with BCrypt password hashing to ensure only authorized personnel can view or modify data.

## 🛠 Tech Stack

* **Backend:** Java 21, Spring Boot 3.4, Spring Security
* **Database:** MySQL 8
* **ORM:** Spring Data JPA (Hibernate)
* **Frontend:** Thymeleaf, Bootstrap 4, JavaScript (Dynamic Charts)
* **Reporting:** PDF Generation (`openhtmltopdf`) with embedded Base64 charts
* **DevOps:** Docker Desktop, Docker Compose

## 🏗 Architecture & Strategy

* **Hybrid Language Strategy:** The backend logic, database schema, REST APIs, and documentation are written in **English** to maintain professional coding standards. The frontend user interface and generated PDF reports are in **Serbian**, tailored for local shop workers and management.
* **Containerized Deployment:** The entire system (Spring Boot app + MySQL database) is orchestrated using a `docker-compose.yml` file, ensuring a consistent environment, easy startup, and secure isolated networking.

## 💻 How to Run Locally

### Prerequisites
* **Docker** and **Docker Compose** (Docker Desktop) installed
* **Git**

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
