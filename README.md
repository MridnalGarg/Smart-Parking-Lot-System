# Smart-Parking-Lot-System

# Automated Parking Lot Management System

A robust, high-concurrency Spring Boot application designed to manage multi-tiered parking lots for different vehicle types (`TWO_WHEELER`, `LMV`, `HMV`). The system dynamically handles high-throughput operations (Check-in, Check-out, and Cancellations) concurrently while ensuring absolute thread safety using Java's `ReentrantLock` and an auto-configuring slot distribution topology.

---

## 🚀 Getting Started & Setup

### Prerequisites
- **Java JDK 17** or higher
- **Maven 3.8+**
- **cURL** or **Postman** (for testing APIs)

### Installation & Run Steps

1. **Clone the Repository:**
   ```bash
   git clone [https://github.com/your-username/parking-lot-management.git](https://github.com/your-username/parking-lot-management.git)
   cd parking-lot-management
   
2. **Configure Environment Parameters:**
    ```bash
   Open src/main/resources/application.properties to modify system capacities or server configurations if necessary.

3. **Build the Application:**
    ````bash
    mvn clean install
4. **Launch the Server:**
    ```bash
    mvn spring-boot:run
    The application will start by default on http://localhost:8080.