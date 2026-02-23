# 🍖 Mesara ERP (Butchery Management System)

A professional ERP solution designed for butchery chains to manage inventory, sales, and stock movements in real-time.

## 🚀 Key Features
- **Real-time Inventory**: Track stock levels across multiple stores (`ProductStock`).
- **Stock Movement Logs**: Complete audit trail of every gram of meat (Purchase, Sale, Waste, Return).
- **Dashboard Analytics**: Visual representation of sales trends and shop performance.
- **Daily Entry System**: Intuitive form for shop workers to report daily results.

## 🛠 Tech Stack
- **Backend**: Java 17, Spring Boot 3.4
- **Database**: MySQL 8
- **ORM**: Spring Data JPA (Hibernate)
- **Frontend**: Thymeleaf, Bootstrap 4, AdminLTE 3, Chart.js
- **Tools**: Lombok, Maven, Git

## 🏗 Architecture
The project follows a **Hybrid Language Strategy**:
- **Backend (Code, Database, Documentation)**: English - for professional standards and scalability.
- **Frontend (User Interface)**: Serbian - adapted for local shop workers.

## 📦 Project Structure
- `com.mesara.app.domain`: Entity models (Store, Product, StockMovement...)
- `com.mesara.app.service`: Business logic and stock calculations.
- `com.mesara.app.controller`: Web and API endpoints.
- `com.mesara.app.repository`: Database access layer.