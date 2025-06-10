**mmdev-repo** 
# Fintech Dynamic Amount Service

A REST API for a fintech application that receives two numeric inputs, adds them, and applies a dynamic percentage retrieved from an external service. It features Redis caching, retry mechanisms, and PostgreSQL storage for call history.

## Features

- **Add Two Numbers**: Accepts two input numbers and returns their sum.
- **Dynamic Percentage**: Queries an external service to get a percentage, which is applied to the sum.
- **Caching with Redis**: Caches the percentage value for 30 minutes to avoid repeated external calls.
- **Retry Logic**: Retries up to 3 times if the external service fails.
- **Fallback**: Uses the cached percentage if available; otherwise, returns an appropriate error.
- **Call History**: Stores all API requests and results in a PostgreSQL database.
- **Dockerized Deployment**: Runs fully in Docker using `docker-compose`.
- **API Documentation**: Available via Swagger UI or Postman collection.

---
> Example:  
> Input: `num1=5`, `num2=5`  
> External Percentage: `10%`  
> Output: `(5 + 5) + 10% = 11.0`

## Tech Stack

- Java 21
- Spring Boot
- Redis (cache)
- PostgreSQL (history storage)
- Docker & Docker Compose
- Swagger for API documentation

---

## Getting Started

### Prerequisites

- Docker and Docker Compose installed
- Ensure that the external percentage service URL is configured in application.properties:
> Example: external.percentage.url=http://external-service/api/percentage

### 1. Clone the Repository

- git clone https://github.com/mmdev-repo/tenpo-dynamic-amount-rest.git
- cd tenpo-dynamic-amount-rest

### 2. Run the Services

- docker-compose up --build

- This will start:

> fintech-sum-service at http://localhost:8080
 
> Redis on port: 6379
 
> PostgreSQL on port: 5432

### 3. API Documentation
- Once the app is running, open:
> http://localhost:8080/swagger-ui.html

This UI lets you explore and test the API interactively.