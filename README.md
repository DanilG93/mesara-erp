# 🍖 Mesara ERP (Butchery Management System)

A professional, production-ready ERP solution designed for butchery chains to manage inventory, sales, and stock movements in real-time.

## 🚀 Key Features

* **Real-time Inventory:** Track stock levels across multiple stores automatically.
* **Stock Movement Logs:** Complete audit trail of every gram of meat (Purchase, Sale, Waste, Return).
* **Dashboard Analytics:** Visual representation of sales trends and shop performance.
* **Daily Entry System:** Intuitive forms for shop workers to report daily results.
* **🔒 Secure Access:** System lockdown using **Spring Security** with BCrypt password hashing to protect business data.
* **☁️ Cloud-Ready:** Fully containerized using **Docker** and deployed continuously.

## 🛠 Tech Stack

* **Backend:** Java 21, Spring Boot 3.4, Spring Security
* **Database:** MySQL 8 (Hosted on Aiven Cloud DBaaS)
* **ORM:** Spring Data JPA (Hibernate)
* **Frontend:** Thymeleaf, Bootstrap 4, AdminLTE 3, Chart.js
* **DevOps & Infrastructure:** Docker, Render (PaaS), Git, Maven
* **Security:** Environment Variables (`.env`) for secret management, BCrypt

## 🛡️ Best Practices Implemented

* **Externalized Configuration:** Zero hardcoded credentials. All sensitive data (database URIs, passwords) are managed via Environment Variables and the `@Value` annotation.
* **Database Management:** Separation of concerns—database is hosted on a secure cloud provider (Aiven), entirely decoupled from the application server.
* **Containerization:** Custom `Dockerfile` implementation ensuring consistent environments from local development to production.

## 🏗 Architecture & Strategy

The project follows a **Hybrid Language Strategy**:
* **Backend (Code, Database, Documentation):** English - ensuring professional standards, readability, and future scalability.
* **Frontend (User Interface):** Serbian - tailored specifically for local shop workers and end-users.

## 📦 Project Structure

* `com.mesara.app.config`: Security and application configuration.
* `com.mesara.app.controller`: Web and API endpoints.
* `com.mesara.app.domain`: Entity models (Store, Product, StockMovement...).
* `com.mesara.app.dto`: Data Transfer Objects for form handling and data transfer.
* `com.mesara.app.repository`: Database access layer (Spring Data JPA).
* `com.mesara.app.service`: Business logic and stock calculations.
* `com.mesara.app.utils`: Helper classes, PDF generators, and utility functions.
* `resources/templates`: Thymeleaf HTML views for the frontend user interface.
* `resources/static`: Static assets (fonts, custom CSS, and JavaScript).

---

## 💻 How to Run Locally

To run this project on your local machine for testing or development, follow these steps:

### Prerequisites
* **Java 21** installed
* **Maven** installed
* An IDE (IntelliJ IDEA, Eclipse, VS Code)

### Steps

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/DanilG93/mesara-erp.git](https://github.com/DanilG93/mesara-erp.git)

2. **Configure Environment Variables:**
For security reasons, database credentials and admin logins are externalized. Before running the app,
you MUST set the following Environment Variables in your IDE's Run Configuration (or your OS environment):

    DB_HOST = (Your MySQL host)
    DB_PORT = (Your MySQL port, e.g., 3306)
    DB_NAME = (Your database name)
    DB_USER = (Your database username)
    DB_PASS = (Your database password)
    ADMIN_USER = (Desired admin username for the web login)
    ADMIN_PASS = (Desired admin password for the web login)

3. **Build and Run:**
    mvn clean install
    mvn spring-boot:run
(Alternatively, just run the MesaraApplication.java main class directly from your IDE).

4. **Access the Application:**
   Open your browser and navigate to http://localhost:8080. You will be greeted by the Spring Security login page.