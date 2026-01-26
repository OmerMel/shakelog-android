
# ShakeLog Backend Server ⚙️

The **ShakeLog Backend** is a RESTful API built with **Spring Boot** and **Kotlin**. It serves as the central hub for the ShakeLog ecosystem, handling data ingestion from the Android SDK, managing project authentication, and serving data to the Web Portal.

## Architecture

The server follows a classic layered architecture to ensure separation of concerns and scalability:

-   **Controller Layer:** Handles HTTP requests and maps JSON to DTOs.
    
-   **Service Layer:** Contains business logic (validations, API Key checks, hashing).
    
-   **Repository Layer:** Manages data persistence using Spring Data MongoDB.
    
-   **Database:** MongoDB Atlas (Cloud) for storing unstructured JSON reports.
    

## Key Features

-   **Report Ingestion:** Receives complex bug reports including screenshots, logs, device metadata, and breadcrumbs.
    
-   **Project Authentication:** Implements a custom "Project-based" auth system (Project ID + Access Code) for teams.
    
-   **Multi-Tenancy:** Uses unique **API Keys** to securely segregate data between different applications.
    
-   **Status Management:** Allows the Web Portal to update bug status (Open, In Progress, Resolved, Closed).
    
-   **☁Cloud Native:** Designed to run on cloud platforms like **Render** .
    

## Tech Stack

-   **Language:** Kotlin (JDK 17+)
    
-   **Framework:** Spring Boot 3.x
    
-   **Build Tool:** Gradle (Kotlin DSL)
    
-   **Database:** MongoDB (Atlas)
    
-   **API Documentation:** Swagger / OpenAPI (Optional integration)
    

## Getting Started

### Prerequisites

-   Java Development Kit (JDK) 17 or higher.
    
-   A MongoDB Atlas account (Free tier is sufficient).
    
-   Git.
    

### 1. Clone the Repository

```
git clone [https://github.com/OmerMel/shakelog-server.git](https://github.com/OmerMel/shakelog-server.git)
cd shakelog-server

```

### 2. Configure Environment

Navigate to `src/main/resources/application.properties`.

You need to set up your MongoDB connection string.

**Ideally, set this as an Environment Variable on your machine or deployment server:**

```
# Database Configuration
spring.data.mongodb.uri=${MONGODB_URI}
spring.data.mongodb.database=shakelog_db

# Server Port (Default 8080)
server.port=8080

```

_If running locally for testing, you can paste your connection string directly (do not commit secrets to Git!):_

`spring.data.mongodb.uri=mongodb+srv://<user>:<password>@cluster.mongodb.net/?retryWrites=true&w=majority`

### 3. Run the Server

Run the application using the Gradle wrapper:

```
./gradlew bootRun

```

The server will start at `http://localhost:8080`.

## API Endpoints

### Authentication & Projects


| Method | Endpoint | Description | Payload |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/create-project` | Create a new project & get API Key | `{ "projectId": "...", "displayName": "...", "accessCode": "..." }` |
| `POST` | `/api/auth/login-project` | Login to the web portal | `{ "projectId": "...", "accessCode": "..." }` |


### Reports (SDK)

| Method | Endpoint | Description | Payload |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/reports` | Submit a new bug report | `{ "apiKey": "...", "reportId": "...", "device": {...}, ... }` |

### Portal Operations

| Method | Endpoint | Description | Payload / Params |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/reports` | Get all reports for a project | `?apiKey=sk_...` |
| `PUT` | `/api/reports/status` | Update report status | `{ "reportId": "...", "status": "CLOSED", "apiKey": "..." }` |


## Data Models

### Project Entity

```
{
  "projectId": "my-app-ios",
  "displayName": "My Cool App",
  "accessCodeHash": "hashed_secret...",
  "apiKey": "sk_live_5f3a..."
}

```

### Report Entity

```
{
  "reportId": "uuid-v4",
  "status": "OPEN",
  "device": {
    "model": "Pixel 6",
    "osVersion": "13",
    "batteryLevel": "85%"
  },
  "screenshotUrl": "https://firebasestorage...",
  "logsUrl": "https://firebasestorage...",
  "breadcrumbs": [...]
}

```

## License

This project is licensed under the MIT - see the [LICENSE](https://github.com/OmerMel/shakelog-server/blob/master/LICENSE) file for details.
